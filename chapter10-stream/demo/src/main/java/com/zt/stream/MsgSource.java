package com.zt.stream;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author keets
 *
 * 声明输出型通道，它的通道名称为 output；
 */
public interface MsgSource {
    String OUTPUT = "msg-output";

    @Output(OUTPUT)
    MessageChannel output();
}
