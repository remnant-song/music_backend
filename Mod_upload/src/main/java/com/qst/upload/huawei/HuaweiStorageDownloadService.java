package com.qst.upload.huawei;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class HuaweiStorageDownloadService {

    private static final Logger log = LoggerFactory.getLogger(HuaweiStorageDownloadService.class);
    @Value("${huawei.agc.region}")
    private String region;
    @Value("${huawei.agc.client-id}")
    private String clientId;
    @Value("${huawei.agc.project-id}")
    private String projectId;
    @Value("${huawei.agc.bucket-name}")
    private String bucketName;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${huawei.agc.credential-path}")
    private String credentialPath;

    public ResponseEntity<StreamingResponseBody> downloadFile(
            String type,
            String filename,
            HttpHeaders originalHeaders) {

        try {
            // 1. 构建华为云下载URL
            String downloadUrl = buildDownloadUrl(type, filename);
            System.out.println("华为云下载URL: "+downloadUrl);
            log.info("代理下载华为云文件: {}", downloadUrl);

            // 2. 创建流式响应体
            StreamingResponseBody stream = outputStream -> {
                HttpURLConnection connection = null;
                InputStream input = null;
                try {
                    // 3. 创建HTTP连接
                    URL url = new URL(downloadUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    // 4. 设置请求头
                    connection.setRequestProperty("client_id", clientId);
                    connection.setRequestProperty("productId", projectId);
                    connection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
                    connection.setRequestProperty("X-Agc-Trace-Id", UUID.randomUUID().toString());

                    // 5. 设置Range头部（如果存在）
                    if (originalHeaders.containsKey(HttpHeaders.RANGE)) {
                        String range = originalHeaders.getRange().get(0).toString();
                        connection.setRequestProperty("Range", range);
                    }

                    // 6. 连接并检查响应
                    connection.connect();
                    int status = connection.getResponseCode();

                    if (status >= 300) {
                        log.error("华为云下载失败: {}", status);
                        throw new RuntimeException("下载失败，状态码: " + status);
                    }

                    // 7. 获取输入流
                    input = connection.getInputStream();

                    // 8. 流式传输数据
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        try {
                            outputStream.write(buffer, 0, bytesRead);
                        } catch (IOException e) {
                            // 处理客户端断开连接
                            log.warn("客户端可能已断开连接: {}", e.getMessage());
                            break;
                        }
                    }
                    outputStream.flush();

                } catch (Exception e) {
                    log.error("下载处理失败", e);
                    throw new RuntimeException(e);
                } finally {
                    // 9. 确保关闭资源
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            log.warn("关闭输入流失败", e);
                        }
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            };

            // 10. 设置响应头
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            responseHeaders.setContentDispositionFormData("attachment", filename);

            // 11. 返回响应
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(stream);

        } catch (Exception e) {
            log.error("代理下载失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    // 构建下载URL
    private String buildDownloadUrl(String type, String filename) {
        String domain = getDomainByRegion(region);
        return "https://" + domain + "/" + bucketName + "/" + type + "/" + filename;
    }

    // 获取区域域名（与上传服务相同）
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

    // 获取访问令牌（与上传服务相同）
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
}