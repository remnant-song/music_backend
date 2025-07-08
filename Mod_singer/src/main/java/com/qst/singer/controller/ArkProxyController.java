package com.qst.singer.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Music;
import com.qst.domain.util.JwtUtils;
import com.qst.singer.service.IMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/singer/api")
public class ArkProxyController {
    @Autowired
    IMusicService musicService;  // 注入音乐服务
    private final WebClient webClient;
    private final String ARK_TOKEN = "5e1bba49-b334-482a-85f9-0b93edab5fd1";

    public ArkProxyController(WebClient.Builder webClientBuilder) {
        System.out.println("ArkProxyController created");
        this.webClient = webClientBuilder.baseUrl("https://ark.cn-beijing.volces.com").build();
    }

    // 新增：歌手音乐总结接口，传入歌手id和分页参数
    @PostMapping("/ark-summary/{pn}/{size}")
    public Mono<String> proxySingerMusicSummary(@PathVariable(value = "pn") Integer pn, @PathVariable(value = "size") Integer size, String keyword, @RequestHeader(value = "Authorization", required = false) String token) {
        System.out.println("proxySingerMusicSummary called");
        if (!StringUtils.hasLength(token)) {
            return Mono.error(new RuntimeException("未提供token"));
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer singerId;
        try {
            singerId = JwtUtils.getMemberIdByJwtToken(token);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("token无效或已过期"));
        }
        System.out.println("size="+size);
        System.out.println("pn="+pn);
        System.out.println("keyword="+keyword);
        System.out.println("singerId="+singerId);
        // 调用音乐服务，获取歌手音乐分页数据
        Mess musicMess = musicService.getMusic(keyword, singerId, pn, size);
        Page<?> musicPage = (Page<?>) musicMess.getData().get("page");
        List<?> musicRecords = musicPage.getRecords();

        // 拼接歌手歌曲信息文本，方便AI分析
        StringBuilder sb = new StringBuilder();
        sb.append("以下是歌手ID ").append(singerId).append(" 的部分歌曲列表：\n");
        for (Object obj : musicRecords) {
            sb.append(obj.toString()).append("\n");
        }
        for (Object obj : musicRecords) {
            if (obj instanceof Music music) {
                sb.append("歌曲名称：").append(music.getMusicName())
                        .append("，上传时间：").append(music.getCreateTime())
                        .append("，播放量：").append(music.getListenNumb())
                        .append("，风格：").append(music.getTagList())
                        .append("\n");
            } else {
                sb.append(obj.toString()).append("\n");
            }
        }
        String userText = "这是一个音乐平台的后台管理系统，请根据下列歌曲数据，生成一份关于该歌手的歌曲运营分析报告，要求内容专业、条理清晰。请遵循以下格式进行撰写：\n\n" +
                "一、整体歌曲概况：统计该歌手的歌曲数量、风格类型、发布时间分布等，并简要说明整体创作特点。\n" +
                "二、代表作品分析：挑选具有代表性或播放量较高的歌曲，分析其特点，如风格、受众、内容等。\n" +
                "三、潜在问题与优化建议：指出歌曲数据中可能存在的问题（如重名、缺少关键信息、风格重复等），并提出改进建议。\n" +
                "四、总结：概括该歌手的歌曲内容整体风格和发展建议。\n\n" +
                "请使用正式、简洁的语言，不使用任何表格，仅输出纯文字分析。\n\n" +
                "以下是该歌手的部分歌曲数据：" + sb;

        Map<String, Object> body = Map.of(
                "model", "doubao-seed-1-6-250615",
                "messages", List.of(
                        Map.of("role", "user", "content", List.of(Map.of("type", "text", "text", userText)))
                )
        );
        System.out.println("body="+body);
//        return webClient.post()
//                .uri("/api/v3/chat/completions")
//                .header("Authorization", "Bearer " + ARK_TOKEN)
//                .header("Content-Type", "application/json")
//                .bodyValue(body)
//                .retrieve()
//                .bodyToMono(String.class);
        Mono<String> responseMono = webClient.post()
                .uri("/api/v3/chat/completions")
                .header("Authorization", "Bearer " + ARK_TOKEN)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);

// 打印返回结果（仅用于调试）
        responseMono.subscribe(response -> System.out.println("AI 返回结果：" + response));
// 返回给前端
        return responseMono;
    }

}
