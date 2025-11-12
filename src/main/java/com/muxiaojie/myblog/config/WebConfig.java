package com.muxiaojie.myblog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/user/login",        // GET 和 POST 都排除
                    "/user/logout",       // 注销路径
                    "/user/register",     // 注册路径
                    "/css/**", 
                    "/js/**", 
                    "/images/**",
                    "/error",
                    "/"                   // 首页也排除，或者根据需求调整
                );
    }
}