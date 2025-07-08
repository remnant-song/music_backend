package com.qst.upload.huawei;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qst.upload.util.MusicDbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class HuaweiStorageCleanService {

    private static final Logger log = LoggerFactory.getLogger(HuaweiStorageCleanService.class);

    @Value("${huawei.agc.region}")
    private String region;
    @Value("${huawei.agc.client-id}")
    private String clientId;
    @Value("${huawei.agc.project-id}")
    private String projectId;
    @Value("${huawei.agc.bucket-name}")
    private String bucketName;
    @Value("${huawei.agc.credential-path}")
    private String credentialPath;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MusicDbUtil musicDbUtil;

    // 缓存的Token及过期时间（复用现有逻辑）
    private String cachedToken = null;
    private long tokenExpirationTime = 0;


    /**
     * 执行清理：删除华为云中不存在于数据库的文件
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void cleanAbandonedFiles() {
        log.info("开始执行华为云废弃文件清理任务...");
        try {
            // 1. 获取数据库中的有效文件路径
            Set<String> validPaths = musicDbUtil.getValidFilePaths();
            log.info("数据库中有效文件数量：{}", validPaths.size());

            // 2. 获取华为云中所有文件路径
            Set<String> cloudFilePaths = listAllCloudFiles();
            log.info("华为云中文件总数：{}", cloudFilePaths.size());

            // 3. 计算需要删除的文件（华为云有但数据库没有）
            Set<String> filesToDelete = new HashSet<>(cloudFilePaths);
            filesToDelete.removeAll(validPaths);
            log.info("需要删除的废弃文件数量：{}", filesToDelete.size());

            // 4. 批量删除文件
            int successCount = 0;
            for (String filePath : filesToDelete) {
                if (deleteCloudFile(filePath)) {
                    successCount++;
                }
            }
            log.info("清理任务完成，成功删除{}个文件，失败{}个文件",
                    successCount, filesToDelete.size() - successCount);

        } catch (Exception e) {
            log.error("清理任务执行失败", e);
        }
    }


    /**
     * 列举华为云存储桶中的所有文件路径（分页遍历）
     */
    private Set<String> listAllCloudFiles() throws Exception {
        Set<String> cloudFilePaths = new HashSet<>();
        String marker = null;
        boolean hasMore = true;

        while (hasMore) {
            Map<String, Object> responseBody = listCloudFiles(marker);
            log.info("当前页响应: {}", responseBody); // 打印当前页响应

            // 注意：替换为步骤1中确认的实际字段名
            // 步骤1：提取文件列表（使用正确的字段名"contents"）
            List<Map<String, Object>> files = (List<Map<String, Object>>) responseBody.get("contents");

            // 步骤2：处理空值（若字段名错误或无文件，files可能为null）
            if (files == null) {
                log.warn("当前页文件列表为空（可能字段名错误或无文件）");
                hasMore = false;
                continue;
            }

            // 步骤3：提取文件路径（使用正确的字段名"key"）
            for (Map<String, Object> file : files) {
                String filePath = (String) file.get("key"); // 改为"key"
                if (filePath != null) {
                    cloudFilePaths.add(filePath);
                    log.debug("提取文件路径: {}", filePath); // 打印提取的每个文件路径

                }
            }
            // 步骤4：处理分页标记（根据实际分页字段名调整，如"NextMarker"）
            marker = (String) responseBody.get("NextMarker"); // 替换为实际分页标记字段名
            hasMore = marker != null && !marker.isEmpty();
            log.info("当前页处理完成，提取文件数: {}, 下一页标记: {}", files.size(), marker);
        }
        return cloudFilePaths;
    }

    /**
     * 调用华为云列举文件接口（单页）
     * 参考华为云"列举文件"接口文档
     */
    private Map<String, Object> listCloudFiles(String marker) throws Exception {
        String domain = getDomainByRegion(region);
        String url = "https://" + domain + "/" + bucketName;

        // 构建查询参数
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParamIfPresent("marker", Optional.ofNullable(marker))
                .queryParam("max-keys", "1000"); // 最大分页大小

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("client_id", clientId);
        headers.set("productId", projectId);
        headers.set("Authorization", "Bearer " + getAccessToken());
        headers.set("X-Agc-Trace-Id", UUID.randomUUID().toString());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // 关键修改：直接将响应解析为Map（而非先转为String）
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // 在listCloudFiles方法中，打印完整响应
        Map<String, Object> responseBody = response.getBody();
        log.info("华为云接口完整响应: {}", responseBody); // 关键：打印完整响应

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("列举文件失败，状态码：" + response.getStatusCodeValue());
        }
        return response.getBody();
    }

    /**
     * 删除华为云中的指定文件
     */
    private boolean deleteCloudFile(String filePath) {
        try {
            String domain = getDomainByRegion(region);
            String url = "https://" + domain + "/" + bucketName + "/" + filePath;

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("client_id", clientId);
            headers.set("productId", projectId);
            headers.set("Authorization", "Bearer " + getAccessToken());
            headers.set("X-Agc-Trace-Id", UUID.randomUUID().toString());

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // 发送DELETE请求
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("成功删除废弃文件：{}", filePath);
                return true;
            } else {
                log.error("删除文件失败，路径：{}，状态码：{}", filePath, response.getStatusCodeValue());
                return false;
            }
        } catch (Exception e) {
            log.error("删除文件异常，路径：{}", filePath, e);
            return false;
        }
    }


    // 以下方法复用自现有代码，统一维护

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

    // 复用现有Token获取逻辑（带缓存）
    private String getAccessToken() {
        // 性能统计
        // 1. 检查缓存是否有效
        if (cachedToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            log.info("使用缓存的华为云Token");
            return cachedToken;
        }

        try {
            String authUrl = getAuthUrlByRegion(region);
            log.info("获取华为云Token URL: {}", authUrl);

            // 读取凭据文件
            String content = new String(Files.readAllBytes(Paths.get(credentialPath)));
            Map<String, String> credential = objectMapper.readValue(content, new TypeReference<>() {
            });

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
}