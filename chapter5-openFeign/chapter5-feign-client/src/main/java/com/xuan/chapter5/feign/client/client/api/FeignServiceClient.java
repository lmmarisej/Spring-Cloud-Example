package com.xuan.chapter5.feign.client.client.api;

import com.xuan.chapter.common.chapter5.dto.Instance;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by xuan on 2018/2/3.
 */
@FeignClient("feign-service")       // 通过@FeignClient注解来指定调用的远程服务名称。
public interface FeignServiceClient {

    @RequestMapping(value = "/instance/{serviceId}", method = RequestMethod.GET)
    Instance getInstanceByServiceId(@PathVariable("serviceId") String serviceId);

    @RequestMapping(value = "/instance/{serviceId}", method = RequestMethod.DELETE)
    String deleteInstanceByServiceId(@PathVariable("serviceId") String serviceId);

    @RequestMapping(value = "/instance", method = RequestMethod.POST)
    String createInstance(@RequestBody Instance instance);

    @RequestMapping(value = "/instance/{serviceId}", method = RequestMethod.PUT)
    String updateInstanceByServiceId(@RequestBody Instance instance, @PathVariable("serviceId") String serviceId);
}
