package com.qst.upload.service;

import com.qst.upload.service.HuaweiStorageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
public class UploadService {
    @Value("${huawei.agc.bucket-name}")
    private String bucketName;

    @Autowired
    private HuaweiStorageUploadService huaweiStorageUploadService;

    public String uploadImage(MultipartFile file) {
        return upLoadFile(file, "image");
    }

    public String uploadLyric(MultipartFile file) {
        return upLoadFile(file, "lyric");
    }

    public String uploadMusic(MultipartFile file) {
        return upLoadFile(file, "music");
    }

    public String upLoadFile(MultipartFile file, String type) {
        try {
            // 生成新文件名
            String newFilename = getNewName(file);
            String objectPath = type + "/" + newFilename;

            // 上传到华为云存储
            huaweiStorageUploadService.uploadFile(file, objectPath);

            // 返回本地代理下载URL
            return "/upload/download/" + type + "/" + newFilename;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
    public String getNewName(MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        String filename = file.getOriginalFilename();
        return uuid + filename.substring(filename.lastIndexOf("."));
    }
}