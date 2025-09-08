package com.qst.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qst.admin.mapper.UserMapper;
import com.qst.admin.service.ILogService;
import com.qst.admin.service.IUserService;
import com.qst.domain.entity.Log;
import com.qst.domain.entity.Mess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/api")
public class ArkProxyController {
    @Autowired
    ILogService logService;

    @Autowired
    IUserService userService;
    private final WebClient webClient;
    private final String ARK_TOKEN = "5e1bba49-b334-482a-85f9-0b93edab5fd1";

    public ArkProxyController(WebClient.Builder webClientBuilder) {
        System.out.println("ArkProxyController created");
        this.webClient = webClientBuilder.baseUrl("https://ark.cn-beijing.volces.com").build();
    }

    @PostMapping("/ark-summary")
    public Mono<String> proxyAutoSummary() {
        // 拉取日志
        Page<Log> logsPage = (Page<Log>) logService.getLog(1, 20).getData().get("logs");
        List<Log> records = logsPage.getRecords();

        // 提取内容
        StringBuilder sb = new StringBuilder();
        for (Log log : records) {
            sb.append("用户「")
                    .append(log.getUserName())
                    .append("」")
                    .append(log.getDoSome());
            if (log.getMusicName() != null && !log.getMusicName().isBlank()) {
                sb.append("《").append(log.getMusicName()).append("》");
            }
            sb.append("，操作时间：").append(log.getCreateDate()).append("\n");
        }

        // 构造 AI 请求
        String userText = "这是一个音乐平台的后台管理系统，请根据下列用户行为日志，生成一份专业、条理清晰的中文分析报告。请注意以下格式与要求：\n" +
                "\n" +
                "输出应分为以下四个部分：\n" +
                "一、整体操作概况：统计各类操作的数量、涉及用户数，并总结操作类型的分布情况。\n" +
                "二、分用户行为分析：按用户ID分别描述其操作类型与行为特征。\n" +
                "三、关键现象与潜在建议：指出可能存在的问题、异常或系统改进建议。\n" +
                "四、总结：概括本次日志所反映的核心运营行为和建议方向。\n" +
                "\n" +
                "禁止使用任何表格，只输出纯文字报告。\n" +
                "\n" +
                "语言应专业、规范、简洁有条理，贴合后台管理系统分析报告的风格。\n" +
                "\n" +
                "以下是用户行为日志：" + sb;

        Map<String, Object> body = Map.of(
                "model", "doubao-seed-1-6-250615",
                "messages", List.of(
                        Map.of("role", "user", "content", List.of(Map.of("type", "text", "text", userText)))
                )
        );
        System.out.println("body="+body);
        return webClient.post()
                .uri("/api/v3/chat/completions")
                .header("Authorization", "Bearer " + ARK_TOKEN)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }
}
