package com.qst.upload.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 创建完整的消息转换器列表
        List<HttpMessageConverter<?>> converters = new ArrayList<>();

        // 1. 添加JSON转换器（处理application/json）
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        converters.add(jsonConverter);

        // 2. 添加其他必要转换器
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(new FormHttpMessageConverter());

        // 3. 设置转换器列表（覆盖默认转换器）
        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }
//    使用PoolingHttpClientConnectionManager管理连接池
//    支持连接重用，减少TCP连接开销
//            配置连接生命周期和空闲超时
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(
            RestTemplateProperties properties) {
        System.out.println("RestTemplateConfig.poolingHttpClientConnectionManager,使用PoolingHttpClientConnectionManager管理连接池");
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager();

        connectionManager.setMaxTotal(properties.getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(properties.getDefaultMaxPerRoute());
        connectionManager.setValidateAfterInactivity(properties.getValidateAfterInactivity());

        return connectionManager;
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager connectionManager,
                                          RestTemplateProperties properties) {
        System.out.println("RestTemplateConfig.httpClient创建请求配置");
        // 创建请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(properties.getConnectTimeout())
                .setConnectionRequestTimeout(properties.getConnectionRequestTimeout())
                .setSocketTimeout(properties.getSocketTimeout()) // 这相当于读取超时
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setConnectionTimeToLive(properties.getTimeToLive(), TimeUnit.MILLISECONDS)/*连接生命周期管理*/
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory(
            CloseableHttpClient httpClient) {
        System.out.println("RestTemplateConfig.clientHttpRequestFactory");
        return new HttpComponentsClientHttpRequestFactory();
    }
}