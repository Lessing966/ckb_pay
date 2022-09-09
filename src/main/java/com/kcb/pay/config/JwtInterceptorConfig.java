package com.kcb.pay.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class JwtInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private com.kcb.pay.config.JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    /**
     * 注册自定义拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/sys/**","/app/**")
                .excludePathPatterns("/app/login");
    }

}
