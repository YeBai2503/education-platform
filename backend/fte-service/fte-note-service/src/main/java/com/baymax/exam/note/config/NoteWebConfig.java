package com.baymax.exam.note.config;

import com.baymax.exam.web.config.BaseWebConfig;
import org.springframework.context.annotation.Configuration;

/**
 * 笔记服务Web配置
 * 继承BaseWebConfig确保加载UserTokenInterceptor拦截器
 */
@Configuration
public class NoteWebConfig extends BaseWebConfig {
    // 默认继承了BaseWebConfig的拦截器配置
}