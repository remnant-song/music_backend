package com.qst.upload.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class MusicDbUtil {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 从数据库提取所有有效文件路径（华为云存储的实际路径，如"image/xxx"）
     */
    public Set<String> getValidFilePaths() {
        Set<String> validPaths = new HashSet<>();

        // 查询music表中所有非空的url字段
        String sql = "SELECT image_url, music_url, lyric FROM music WHERE image_url IS NOT NULL " +
                "OR music_url IS NOT NULL OR lyric IS NOT NULL";
        jdbcTemplate.query(sql, rs -> {
            // 处理每个URL，提取华为云实际文件路径
            extractFilePath(rs.getString("image_url"), validPaths);
            extractFilePath(rs.getString("music_url"), validPaths);
            extractFilePath(rs.getString("lyric"), validPaths);
        });
        String sql2 = "SELECT image_url FROM user WHERE image_url IS NOT NULL";
        jdbcTemplate.query(sql2, rs -> {
            // 处理每个URL，提取华为云实际文件路径
            extractFilePath(rs.getString("image_url"), validPaths);
        });
        return validPaths;
    }

    /**
     * 解析URL，提取华为云存储的实际文件路径
     * 示例：数据库URL为"/upload/download/music/xxx.mp3" → 实际路径为"music/xxx.mp3"
     */
    private void extractFilePath(String url, Set<String> validPaths) {
        if (url == null || url.isEmpty()) {
            return;
        }
        // 截取华为云实际路径（去除前缀"/upload/download/"）
        String prefix = "/upload/download/";
        if (url.startsWith(prefix)) {
            String filePath = url.substring(prefix.length());
            validPaths.add(filePath);
        }
    }
}