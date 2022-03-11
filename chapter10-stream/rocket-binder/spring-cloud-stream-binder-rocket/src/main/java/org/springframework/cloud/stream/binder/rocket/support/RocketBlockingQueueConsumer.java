package org.springframework.cloud.stream.binder.rocket.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.commons.codec.Charsets;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.rocket.properties.RocketConsumerProperties;
import org.springframework.integration.support.MessageBuilderFactory;
import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class RocketBlockingQueueConsumer {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    private ObjectMapper mapper;
    private RocketMQResourceManager resourceManager;
    private String topic;
    private String consumerGroup;
    private ExtendedConsumerProperties<RocketConsumerProperties> extendedConsumerProperties;
    private DefaultMQPushConsumer consumer;
    private MessageBuilderFactory messageBuilderFactory;

    private final BlockingQueue<RocketDelivery> queue;
    private volatile ShutdownSignalException shutdown;
    private ObjectMapper objectMapper = new ObjectMapper();


    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void setResourceManager(RocketMQResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public void setExtendedConsumerProperties(ExtendedConsumerProperties<RocketConsumerProperties> extendedConsumerProperties) {
        this.extendedConsumerProperties = extendedConsumerProperties;
    }

    public void setConsumer(DefaultMQPushConsumer consumer) {
        this.consumer = consumer;
    }

    public void setMessageBuilderFactory(MessageBuilderFactory messageBuilderFactory) {
        this.messageBuilderFactory = messageBuilderFactory;
    }

    public void setShutdown(ShutdownSignalException shutdown) {
        this.shutdown = shutdown;
    }


    public RocketBlockingQueueConsumer(int prefetchCount) {
        super();
        this.queue = new LinkedBlockingQueue<RocketDelivery>(prefetchCount);
    }

    public void start() {
        String nameSrvConnectionString = this.resourceManager.getConfigurationProperties().getNameSrvConnectionString();
        String tags = this.extendedConsumerProperties.getExtension().getTags();
        consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(nameSrvConnectionString);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.setMessageModel(MessageModel.BROADCASTING);
        try {
            consumer.subscribe(this.topic, tags);
            consumer.registerMessageListener(new InnerConsumer());
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public boolean hasDelivery() {
        return true;
    }

    public boolean cancelled() {
        return true;
    }

    public Message nextMessage(long timeout) throws InterruptedException, ShutdownSignalException {
        Message message = handle(this.queue.poll(timeout, TimeUnit.MILLISECONDS));
        if (message == null) {
            throw new ConsumerCancelledException();
        }
        return message;
    }

    private Message handle(RocketDelivery delivery) throws InterruptedException {
        if ((delivery == null && this.shutdown != null)) {
            throw this.shutdown;
        }
        if (delivery == null) {
            return null;
        }
        return delivery.getMessage();
    }

    private final class InnerConsumer implements MessageListenerConcurrently {

        public InnerConsumer() {
            super();
        }

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            for (MessageExt messageExt : list) {
                byte[] payload = messageExt.getBody();
                Map<String, String> headers = messageExt.getProperties();
                logger.info("Listener:" + new String(payload, StandardCharsets.UTF_8));
                try {

                    Map<String, String> userHeaders = new HashMap<>();
                    for (String key : headers.keySet()) {
                        if (key.startsWith("USERS_")) {
                            String val = headers.get(key);
                            String originKey = key.replace("USERS_", "");
                            userHeaders.put(originKey, val);
                        }
                    }

                    String jsonStr = new String(payload, Charsets.UTF_8);
                    org.springframework.messaging.Message<?> internalMsgObject = messageBuilderFactory.withPayload(payload).copyHeaders(userHeaders).build();

                    RocketBlockingQueueConsumer.this.queue.put(new RocketDelivery(internalMsgObject));
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("==========" + e.getMessage());
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }


}
