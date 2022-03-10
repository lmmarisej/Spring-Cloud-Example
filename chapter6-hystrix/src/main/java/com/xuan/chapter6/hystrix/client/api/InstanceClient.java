package com.xuan.chapter6.hystrix.client.api;

import com.xuan.chapter.common.chapter5.dto.Instance;
import com.xuan.chapter6.hystrix.client.fallback.InstanceClientFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by xuan on 2018/3/9.
 */
// value指定远程服务实例名
@FeignClient(value = "feign-service", fallback = InstanceClientFallBack.class)
public interface InstanceClient {

    @RequestMapping(value = "/instance/{serviceId}", method = RequestMethod.GET)
    Instance getInstanceByServiceId(@PathVariable("serviceId") String serviceId);
}
