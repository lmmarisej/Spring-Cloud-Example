package com.blueskykong.config.client.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by keets on 2018/1/7.
 */
@RestController
@RequestMapping("/cloud")
@RefreshScope           // 支持配置刷新
public class TestController {

    @Value("${cloud.version}")          // 将从git-》config-server-》中获取
    private String version;

    @GetMapping("/version")
    public String getVersion() {
        return version;
    }
}
