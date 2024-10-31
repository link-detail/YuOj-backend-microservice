package com.liu.yuojbackendgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//开启nacos注解
@EnableDiscoveryClient
public class YuOjBackendGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run (YuOjBackendGatewayApplication.class, args);
    }

}
