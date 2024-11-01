package com.liu.yuojbackendjudgeservice;

import com.liu.yuojbackendjudgeservice.judge.rabbitmq.MqInitMain;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
//比如：全局异常处理器是一个bean，但是和这个用户模块不是在一个模块，就导致在此模块中找不到这个bean，需要使用组件扫描器去扫描
//这就是需要统一类名的原因：com.liu.xxx
@ComponentScan("com.liu")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.liu.yuojbackendserviceclient.service")
public class YuOjBackendJudgeServiceApplication {

    public static void main(String[] args) {
        //初始化消息队列
        MqInitMain.doInit ();
        SpringApplication.run (YuOjBackendJudgeServiceApplication.class, args);
    }

}
