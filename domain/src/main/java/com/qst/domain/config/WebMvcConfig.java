package com.qst.domain.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
//CORS 配置同时设置了
//✅ allowedOrigins("*")（表示允许任意来源）
//✅ allowCredentials(true)（允许携带 cookie 等凭据）
//但 Spring MVC 从 5.3 开始就禁止在 allowCredentials=true 时再用 allowedOrigins("*")
//因为这在 HTTP 规范中是冲突的
// （浏览器不允许给 Access-Control-Allow-Origin: * 的响应设置 Access-Control-Allow-Credentials: true）。
//Spring 在 5.3 及以上版本（而你用的是 Spring 6.1）中推荐使用 allowedOriginPatterns 来支持匹配所有域名，同时允许携带凭据。
//它会在响应中动态把 Access-Control-Allow-Origin 设置为请求方的 Origin，而不是简单地 *，所以符合 HTTP 规范。
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOriginPatterns("*")       // ✅ 用 allowedOriginPatterns 替代 allowedOrigins
                    .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                    .maxAge(3600)
                    .allowCredentials(true);
        }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /file/image/** 映射到 D:/file/image/ 目录
        registry.addResourceHandler("/file/image/**")
                .addResourceLocations("file:D:/file/image/");
        registry.addResourceHandler("/file/music/**")
                .addResourceLocations("file:D:/file/music/");
        registry.addResourceHandler("/file/lyric/**")
                .addResourceLocations("file:D:/file/lyric/");
    }
}