package com.baymax.exam.experiment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 实验服务启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.baymax.exam.user.feign", "com.baymax.exam.file.feign"})
@MapperScan("com.baymax.exam.experiment.mapper")
@ComponentScan(basePackages = {"com.baymax.exam"})
public class ExperimentApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ExperimentApplication.class, args);
    }
}