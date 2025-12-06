package com.baymax.exam.experiment.config;

import com.baymax.exam.web.config.BaseWebConfig;
import org.springframework.context.annotation.Configuration;

/**
 * 实验服务Web配置类
 * 继承BaseWebConfig确保加载UserTokenInterceptor拦截器
 */
@Configuration
public class WebConfig extends BaseWebConfig {
    // 默认继承了BaseWebConfig的拦截器配置，确保UserTokenInterceptor被正确注册
} 