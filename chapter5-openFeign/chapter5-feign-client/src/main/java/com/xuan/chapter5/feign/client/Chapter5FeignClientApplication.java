package com.xuan.chapter5.feign.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients   // 只有使用了该注解 ， OpenFeign相关的组件和 配置机制才会生效。
public class Chapter5FeignClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(Chapter5FeignClientApplication.class, args);
    }
}
