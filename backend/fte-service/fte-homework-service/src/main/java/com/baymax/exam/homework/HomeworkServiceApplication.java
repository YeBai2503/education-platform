package com.baymax.exam.homework;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * <p>
 * 作业服务启动类
 * </p>
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.baymax.exam"})
@ComponentScan(basePackages = {"com.baymax.exam"})
@MapperScan("com.baymax.exam.homework.mapper")
@Import(RedisAutoConfiguration.class)
public class HomeworkServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(HomeworkServiceApplication.class, args);
    }
}
