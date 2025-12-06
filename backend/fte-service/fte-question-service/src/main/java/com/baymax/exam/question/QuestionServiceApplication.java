package com.baymax.exam.question;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description：问题中心服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.baymax.exam"})
@EnableFeignClients(basePackages="com.baymax.exam")
@ComponentScan(basePackages = {"com.baymax.exam"})
@MapperScan(basePackages = "com.baymax.exam.question.mapper")
public class QuestionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuestionServiceApplication.class, args);
    }
} 