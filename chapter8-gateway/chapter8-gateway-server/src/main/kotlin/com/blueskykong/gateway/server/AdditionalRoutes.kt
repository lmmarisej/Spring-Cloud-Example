package com.blueskykong.gateway.server

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class AdditionalRoutes {

    /*
    * 当请求的路径是/image/png，将会转发到 htφ://blueskykong.com， 其响应头中 加上了 X-TestHeader: foobar 头部 。
    * */
    @Bean
    open fun additionalRouteLocator(builder: RouteLocatorBuilder): RouteLocator = builder.routes {
        route(id = "test-kotlin") {
            host("kotlin.abc.org") and path("/image/png")
            filters {
                addResponseHeader("X-TestHeader", "foobar")
            }
            uri("http://httpbin.org:80")
        }
    }

}