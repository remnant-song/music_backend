package com.qst.upload.huawei;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
//
///**
// * 华为云存储文件上传服务
// */
@Service
public class HuaweiStorageUploadService {
    private static final Logger log = LoggerFactory.getLogger(HuaweiStorageUploadService.class);

    @Value("${huawei.agc.region}")
    private String region;

    @Value("${huawei.agc.client-id}")
    private String clientId;

    @Value("${huawei.agc.project-id}")
    private String projectId;

    @Value("${huawei.agc.credential-path}")
    private String credentialPath;
    @Value("${huawei.agc.bucket-name}")
    private String bucketName;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    /**
     * 上传文件到华为云存储
     */

    public String uploadFile(MultipartFile file, String objectPath) {
        try {
            // 构建上传URL
            String url = buildUploadUrl(objectPath);
            log.info("华为云文件上传URL: {}", url);

            // 准备请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("client_id", clientId);
            headers.set("productId", projectId);
            headers.set("Authorization", "Bearer " + getAccessToken());
            headers.set("X-Agc-File-Size", String.valueOf(file.getSize()));
            headers.set("X-Agc-Trace-Id", UUID.randomUUID().toString());

            // 使用字节数组创建请求实体
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            // 执行PUT请求
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    Map.class
            );
            // 处理响应
            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("文件上传成功，华为云URL: {}", url);
                return url;
            } else {
                log.error("华为云文件上传失败，状态码: {}", response.getStatusCodeValue());
                throw new RuntimeException("文件上传失败");
            }
        } catch (Exception e) {
            log.error("华为云文件上传异常", e);
            throw new RuntimeException("文件上传失败", e);
        }
    }
    /**
     * 构建上传URL
     */
    private String buildUploadUrl(String objectPath) {
        String domain = getDomainByRegion(region);
        return "https://" + domain + "/" + bucketName + "/" + objectPath;
    }
    /**
     * 根据区域获取域名
     */
    private String getDomainByRegion(String region) {
        Map<String, String> domainMap = Map.of(
                "CN", "ops-server-drcn.agcstorage.link/v0",
                "DE", "ops-server-dre.agcstorage.link/v0",
                "SG", "ops-server-dra.agcstorage.link/v0",
                "RU", "ops-server-drru.agcstorage.link/v0"
        );
        String domain = domainMap.get(region.toUpperCase());
        if (domain == null) {
            throw new IllegalArgumentException("不支持的区域: " + region);
        }
        return domain;
    }

    /**
     * 获取访问令牌
     */
    private String getAccessToken() {
        try {
            String authUrl = getAuthUrlByRegion(region);
            log.info("获取华为云Token URL: {}", authUrl);

            // 读取凭据文件
            String content = new String(Files.readAllBytes(Paths.get(credentialPath)));
            Map<String, String> credential = objectMapper.readValue(content, new TypeReference<Map<String, String>>() {});

            // 准备请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // 准备请求体
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("grant_type", "client_credentials");
            requestBody.put("client_id", credential.get("client_id"));
            requestBody.put("client_secret", credential.get("client_secret"));

            // 创建请求实体
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 发送请求（使用exchange方法）
            ResponseEntity<Map> response = restTemplate.exchange(
                    authUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            // 处理响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String token = (String) response.getBody().get("access_token");
                log.info("成功获取华为云Token");
                return token;
            } else {
                log.error("获取华为云Token失败，状态码: {}", response.getStatusCodeValue());
                throw new RuntimeException("获取华为云Token失败");
            }
        } catch (Exception e) {
            log.error("获取华为云Token异常", e);
            throw new RuntimeException("获取Token失败", e);
        }
    }
    /**
     * 根据区域获取Token接口URL
     */
    private String getAuthUrlByRegion(String region) {
        Map<String, String> authUrlMap = Map.of(
                "CN", "https://connect-api.cloud.huawei.com/api/oauth2/v1/token",
                "DE", "https://connect-api-dre.cloud.huawei.com/api/oauth2/v1/token",
                "SG", "https://connect-api-dra.cloud.huawei.com/api/oauth2/v1/token",
                "RU", "https://connect-api-drru.cloud.huawei.com/api/oauth2/v1/token"
        );
        String url = authUrlMap.get(region.toUpperCase());
        if (url == null) {
            throw new IllegalArgumentException("不支持的区域: " + region);
        }
        return url;
    }
    private String buildDownloadUrl(String objectPath) {
        String domain = getDomainByRegion(region);
        return "https://" + domain + "/" + bucketName + "/" + objectPath;
    }
}