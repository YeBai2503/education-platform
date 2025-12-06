package com.baymax.exam.note;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author ：Baymax
 * @date ：Created in 2025/07/04
 * @description：笔记服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.baymax.exam")
@EnableFeignClients(basePackages="com.baymax.exam.*")
//扫描mapper
@MapperScan(basePackages = "com.baymax.exam.note.mapper")
public class NoteServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoteServiceApplication.class, args);
    }
} 