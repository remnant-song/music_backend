package com.qst.upload.huawei;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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

    // 性能统计
    private long tokenFetchCount = 0;
    private long tokenCacheHitCount = 0;
    private long totalTokenFetchTime = 0;
    private long totalDownloadTime = 0;
    private long totalDownloadBytes = 0;
    private long totalRequests = 0;

    // 在类中添加缓存字段
    private String cachedToken = null;
    private long tokenExpirationTime = 0; // 过期时间戳
//    public ResponseEntity<StreamingResponseBody> downloadFile(
//            String type,
//            String filename,
//            HttpHeaders originalHeaders) {
//
//        try {
//            // 1. 构建华为云下载URL
//            String downloadUrl = buildDownloadUrl(type, filename);
//            System.out.println("华为云下载URL: "+downloadUrl);
//            log.info("代理下载华为云文件: {}", downloadUrl);
//
//            // 2. 创建流式响应体
//            StreamingResponseBody stream = outputStream -> {
//                HttpURLConnection connection = null;
//                InputStream input = null;
//                try {
//                    // 3. 创建HTTP连接
//                    URL url = new URL(downloadUrl);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//
//                    // 4. 设置请求头
//                    connection.setRequestProperty("client_id", clientId);
//                    connection.setRequestProperty("productId", projectId);
//                    connection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
//                    connection.setRequestProperty("X-Agc-Trace-Id", UUID.randomUUID().toString());
//
//                    // 5. 设置Range头部（如果存在）
//                    if (originalHeaders.containsKey(HttpHeaders.RANGE)) {
//                        String range = originalHeaders.getRange().get(0).toString();
//                        connection.setRequestProperty("Range", range);
//                    }
//
//                    // 6. 连接并检查响应
//                    connection.connect();
//                    int status = connection.getResponseCode();
//
//                    if (status >= 300) {
//                        log.error("华为云下载失败: {}", status);
//                        throw new RuntimeException("下载失败，状态码: " + status);
//                    }
//
//                    // 7. 获取输入流
//                    input = connection.getInputStream();
//
//                    // 8. 流式传输数据
//                    byte[] buffer = new byte[8192];
//                    int bytesRead;
//                    while ((bytesRead = input.read(buffer)) != -1) {
//                        try {
//                            outputStream.write(buffer, 0, bytesRead);
//                        } catch (IOException e) {
//                            // 处理客户端断开连接
//                            log.warn("客户端可能已断开连接: {}", e.getMessage());
//                            break;
//                        }
//                    }
//                    outputStream.flush();
//
//                } catch (Exception e) {
//                    log.error("下载处理失败", e);
//                    throw new RuntimeException(e);
//                } finally {
//                    // 9. 确保关闭资源
//                    if (input != null) {
//                        try {
//                            input.close();
//                        } catch (IOException e) {
//                            log.warn("关闭输入流失败", e);
//                        }
//                    }
//                    if (connection != null) {
//                        connection.disconnect();
//                    }
//                }
//            };
//
//            // 10. 设置响应头
//            HttpHeaders responseHeaders = new HttpHeaders();
//            responseHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            responseHeaders.setContentDispositionFormData("attachment", filename);
//
//            // 11. 返回响应
//            return ResponseEntity.ok()
//                    .headers(responseHeaders)
//                    .body(stream);
//
//        } catch (Exception e) {
//            log.error("代理下载失败", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
public ResponseEntity<StreamingResponseBody> downloadFile(
        String type,
        String filename,
        HttpHeaders originalHeaders) {

    // 创建性能计时器
    StopWatch stopWatch = new StopWatch(filename);
//    stopWatch.start("total");
    totalRequests++;

    try {
        stopWatch.start("build-url");
        // 1. 构建华为云下载URL
        String downloadUrl = buildDownloadUrl(type, filename);
        stopWatch.stop();
        log.info("代理下载华为云文件: {}", downloadUrl);

        stopWatch.start("create-connection");
        // 2. 创建HTTP连接
        URL url = new URL(downloadUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        stopWatch.stop();

        stopWatch.start("get-token");
        // 3. 使用缓存Token
        String token = getAccessToken();
        stopWatch.stop();

        // 3. 设置认证头
        connection.setRequestProperty("client_id", clientId);
        connection.setRequestProperty("productId", projectId);
        connection.setRequestProperty("Authorization", "Bearer " + token/*getAccessToken()*/);
        connection.setRequestProperty("X-Agc-Trace-Id", UUID.randomUUID().toString());

        // 4. 处理Range请求
        if (originalHeaders.containsKey(HttpHeaders.RANGE)) {
            String rangeHeader = originalHeaders.getFirst(HttpHeaders.RANGE);
            connection.setRequestProperty("Range", rangeHeader);
        }

        // 5. 连接服务器
        stopWatch.start("connect-server");
        connection.connect();
        stopWatch.stop();

        // 6. 获取响应状态码
        int statusCode = connection.getResponseCode();

        // 7. 准备响应头
        HttpHeaders responseHeaders = new HttpHeaders();

        // 8. 添加内容类型（根据文件类型）
        String contentType = getContentType(filename);
        responseHeaders.setContentType(MediaType.parseMediaType(contentType));

        // 9. 添加支持Range的响应头
        responseHeaders.set("Accept-Ranges", "bytes");

        // 10. 处理部分内容响应(206)
        if (statusCode == HttpURLConnection.HTTP_PARTIAL) {
            String contentRange = connection.getHeaderField("Content-Range");
            responseHeaders.set("Content-Range", contentRange);
        }

        // 11. 添加内容长度（如果可用）
        String contentLength = connection.getHeaderField("Content-Length");
        if (contentLength != null) {
            responseHeaders.setContentLength(Long.parseLong(contentLength));
        }

        stopWatch.start("create-stream");
        // 12. 创建流式响应体
        InputStream inputStream = connection.getInputStream();
        StreamingResponseBody stream = outputStream -> {
            try (inputStream) {
//                大缓冲区传输（32KB 替代 8KB）
                byte[] buffer = new byte[32768];
                int bytesRead;

                long bytesTransferred = 0;
                long startTransfer = System.currentTimeMillis();

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    bytesTransferred += bytesRead;
                }

                // 记录传输性能
                long transferTime = System.currentTimeMillis() - startTransfer;
                totalDownloadBytes += bytesTransferred;
                log.info("文件传输完成: {} | 大小: {} KB | 耗时: {} ms | 速率: {} MB/s",
                        filename,
                        bytesTransferred / 1024,
                        transferTime,
                        String.format("%.2f", (bytesTransferred / 1024.0 / 1024.0) / (transferTime / 1000.0))
                );

            } catch (IOException e) {
                log.warn("客户端可能已断开连接: {}", e.getMessage());
            } finally {
                connection.disconnect();
            }
        };
        stopWatch.stop();

        // 13. 返回响应
        return ResponseEntity.status(statusCode)
                .headers(responseHeaders)
                .body(stream);

    } catch (Exception e) {
        log.error("代理下载失败", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }finally {
        // 记录总耗时
        if (stopWatch.isRunning()) {
            stopWatch.stop(); // 这里尝试停止可能未运行的计时器
        }
        totalDownloadTime += stopWatch.getTotalTimeMillis();
        log.info("请求处理耗时统计:\n{}", stopWatch.prettyPrint());
        logPerformanceSummary();
    }

}

    // 根据文件扩展名获取内容类型
    private String getContentType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        switch (extension) {
            case "mp3": return "audio/mpeg";
            case "wav": return "audio/wav";
            case "ogg": return "audio/ogg";
            case "flac": return "audio/flac";
            case "m4a": return "audio/mp4";
            default: return "application/octet-stream";
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
//    private String getAccessToken() {
//        try {
//            String authUrl = getAuthUrlByRegion(region);
//            log.info("获取华为云Token URL: {}", authUrl);
//
//            // 读取凭据文件
//            String content = new String(Files.readAllBytes(Paths.get(credentialPath)));
//            Map<String, String> credential = objectMapper.readValue(content, new TypeReference<Map<String, String>>() {});
//
//            // 准备请求头
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//
//            // 准备请求体
//            Map<String, String> requestBody = new HashMap<>();
//            requestBody.put("grant_type", "client_credentials");
//            requestBody.put("client_id", credential.get("client_id"));
//            requestBody.put("client_secret", credential.get("client_secret"));
//
//            // 创建请求实体
//            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
//
//            // 发送请求（使用exchange方法）
//            ResponseEntity<Map> response = restTemplate.exchange(
//                    authUrl,
//                    HttpMethod.POST,
//                    requestEntity,
//                    Map.class
//            );
//
//            // 处理响应
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                String token = (String) response.getBody().get("access_token");
//                log.info("成功获取华为云Token");
//                return token;
//            } else {
//                log.error("获取华为云Token失败，状态码: {}", response.getStatusCodeValue());
//                throw new RuntimeException("获取华为云Token失败");
//            }
//        } catch (Exception e) {
//            log.error("获取华为云Token异常", e);
//            throw new RuntimeException("获取Token失败", e);
//        }
//    }
    // 修改后的 getAccessToken 方法
    private String getAccessToken() {
        // 性能统计
        long startTime = System.currentTimeMillis();
        tokenFetchCount++;

        // 1. 检查缓存是否有效
        if (cachedToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            tokenCacheHitCount++;
            long duration = System.currentTimeMillis() - startTime;
            log.info("使用缓存的华为云Token");
            return cachedToken;
        }

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

            // 发送请求
            ResponseEntity<Map> response = restTemplate.exchange(
                    authUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            // 处理响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String token = (String) response.getBody().get("access_token");
                Integer expiresIn = (Integer) response.getBody().get("expires_in");

                // 2. 更新缓存和过期时间（提前5分钟过期）
                if (token != null && expiresIn != null) {
                    cachedToken = token;
                    // 华为云通常返回3600秒，提前5分钟刷新
                    tokenExpirationTime = System.currentTimeMillis() + (expiresIn - 300) * 1000L;
                    log.info("成功获取并缓存华为云Token，有效期至: {}",
                            new Date(tokenExpirationTime).toString());

                    // 记录性能
                    long duration = System.currentTimeMillis() - startTime;
                    totalTokenFetchTime += duration;
                    log.info("获取华为云Token | 耗时: {} ms | 有效期: {}秒", duration, expiresIn);

                    return token;
                }
            }

            log.error("获取华为云Token失败，状态码: {}", response.getStatusCodeValue());
            throw new RuntimeException("获取华为云Token失败");

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

    // 添加性能摘要方法
    private void logPerformanceSummary() {
        if (totalRequests % 10 == 0) { // 每10次请求打印一次摘要
            log.info("\n============== 性能摘要 ==============\n" +
                            "总请求数: {}\n" +
                            "Token获取次数: {}\n" +
                            "Token缓存命中率: {}%\n" +
                            "平均Token获取时间: {} ms\n" +
                            "总下载数据量: {} MB\n" +
                            "平均下载速率: {} MB/s\n" +
                            "=====================================",
                    totalRequests,
                    tokenFetchCount,
                    totalRequests > 0 ? (tokenCacheHitCount * 100 / totalRequests) : 0,
                    tokenFetchCount > 0 ? (totalTokenFetchTime / tokenFetchCount) : 0,
                    totalDownloadBytes / (1024 * 1024),
                    totalRequests > 0 ?
                            String.format("%.2f",
                                    (totalDownloadBytes / (1024.0 * 1024.0)) /
                                            (totalDownloadTime / 1000.0)) : 0
            );
        }
    }
}