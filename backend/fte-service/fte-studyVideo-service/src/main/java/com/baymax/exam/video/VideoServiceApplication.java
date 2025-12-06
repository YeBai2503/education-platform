package com.baymax.exam.video;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 视频服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.baymax.exam")
@EnableFeignClients(basePackages = "com.baymax.exam.*")
@MapperScan(basePackages = "com.baymax.exam.video.mapper")
public class VideoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoServiceApplication.class, args);
    }
} 