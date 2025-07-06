package com.qst.upload.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.rest.template.request-factory")
public class RestTemplateProperties {

    private int maxTotal = 200;
    private int defaultMaxPerRoute = 50;
    private int validateAfterInactivity = 5000;
    private int timeToLive = 300000; // 5分钟
    private int connectTimeout = 5000; // 连接超时
    private int connectionRequestTimeout = 3000; // 请求超时
    private int socketTimeout = 30000; // 读取超时（socket超时）

    // Getters and Setters
    public int getMaxTotal() {
        System.out.println("RestTemplateProperties.maxTotal="+maxTotal);
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        System.out.println("RestTemplateProperties.maxTotal="+maxTotal);
        this.maxTotal = maxTotal;
    }

    public int getDefaultMaxPerRoute() {
        System.out.println("RestTemplateProperties.defaultMaxPerRoute="+defaultMaxPerRoute);
        return defaultMaxPerRoute;
    }

    public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        System.out.println("RestTemplateProperties.defaultMaxPerRoute="+defaultMaxPerRoute);
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public int getValidateAfterInactivity() {
        System.out.println("RestTemplateProperties.validateAfterInactivity="+validateAfterInactivity);
        return validateAfterInactivity;
    }

    public void setValidateAfterInactivity(int validateAfterInactivity) {
        System.out.println("RestTemplateProperties.validateAfterInactivity="+validateAfterInactivity);
        this.validateAfterInactivity = validateAfterInactivity;
    }

    public int getTimeToLive() {
        System.out.println("RestTemplateProperties.timeToLive="+timeToLive);
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        System.out.println("RestTemplateProperties.timeToLive="+timeToLive);
        this.timeToLive = timeToLive;
    }

    public int getConnectTimeout() {
        System.out.println("RestTemplateProperties.connectTimeout="+connectTimeout);
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        System.out.println("RestTemplateProperties.connectTimeout="+connectTimeout);
        this.connectTimeout = connectTimeout;
    }

    public int getConnectionRequestTimeout() {
        System.out.println("RestTemplateProperties.connectionRequestTimeout="+connectionRequestTimeout);
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        System.out.println("RestTemplateProperties.connectionRequestTimeout="+connectionRequestTimeout);
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getSocketTimeout() {
        System.out.println("RestTemplateProperties.socketTimeout="+socketTimeout);
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        System.out.println("RestTemplateProperties.socketTimeout="+socketTimeout);
        this.socketTimeout = socketTimeout;
    }
}