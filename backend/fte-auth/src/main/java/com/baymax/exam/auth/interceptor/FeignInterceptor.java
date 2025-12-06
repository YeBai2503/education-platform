package com.baymax.exam.auth.interceptor;

import com.baymax.exam.common.core.base.SecurityConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

//Feign拦截器

@Slf4j
@Component
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 新增一个header
        requestTemplate.header(SecurityConstants.FROM,SecurityConstants.FROM_IN);
        log.info("feign拦截器生效了!");
    }
}
