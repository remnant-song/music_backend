package com.qst.gateway.config;

import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//在 music_gateway 模块里用的是 Spring Cloud Gateway，而 Gateway 默认基于 WebFlux
//Spring Cloud Gateway 默认是基于 WebFlux 的，走 reactive 模型，
// 所以一定要用 WebFlux 专用的 CorsWebFilter 和 reactive 包下的配置类。

@Configuration
public class CorsAutoConfiguration {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedMethod("*");
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }

//    @Bean
//    public ServerCodecConfigurer serverCodecConfigurer() {
//        return new DefaultServerCodecConfigurer();
//    }

    @Bean
    public RouteDefinitionLocator discoveryClientRouteDefinitionLocator(ReactiveDiscoveryClient reactiveDiscoveryClient,
                                                                        DiscoveryLocatorProperties properties) {
        return new DiscoveryClientRouteDefinitionLocator(reactiveDiscoveryClient, properties);
    }
//    DiscoveryClient（org.springframework.cloud.client.discovery.DiscoveryClient）是老的同步阻塞模型。
//    ReactiveDiscoveryClient（org.springframework.cloud.client.discovery.ReactiveDiscoveryClient）
//    是 Spring WebFlux/Gateway 下基于响应式的模型，必须用它。
//    现在的 Spring Cloud Gateway 是 reactive，所以你需要 ReactiveDiscoveryClient。
}
