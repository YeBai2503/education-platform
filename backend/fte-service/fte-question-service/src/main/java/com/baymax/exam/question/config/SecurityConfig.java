package com.baymax.exam.question.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @description：Security配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            // 开放API文档接口
            .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/doc.html").permitAll()
            // 除上面外的所有请求都需要认证
            .anyRequest().permitAll();  // 开发阶段设置为permitAll，实际环境应改为authenticated
            
        return http.build();
    }
} 