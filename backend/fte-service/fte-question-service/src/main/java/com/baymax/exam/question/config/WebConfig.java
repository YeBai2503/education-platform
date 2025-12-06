package com.baymax.exam.question.config;

import com.baymax.exam.web.config.BaseWebConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @description：问题中心Web配置类
 */
@Configuration
public class WebConfig extends BaseWebConfig {
    // 默认继承了BaseWebConfig的拦截器配置，确保UserTokenInterceptor被正确注册
} 