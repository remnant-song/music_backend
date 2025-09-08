package com.qst.upload.controller;

import com.qst.upload.service.HuaweiStorageDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/upload/download")
public class DownloadController {

    @Autowired
    private HuaweiStorageDownloadService huaweiStorageDownloadService;

    @GetMapping("/image/{filename}")
    public ResponseEntity<StreamingResponseBody> downloadImage(
            @PathVariable String filename, @RequestHeader HttpHeaders headers) {
        System.out.println("下载图片"+filename);
        return huaweiStorageDownloadService.downloadFile("image", filename, headers);
    }

    @GetMapping("/lyric/{filename}")
    public ResponseEntity<StreamingResponseBody> downloadLyric(
            @PathVariable String filename, @RequestHeader HttpHeaders headers) {
        System.out.println("下载歌词"+filename);
        return huaweiStorageDownloadService.downloadFile("lyric", filename, headers);
    }

    @GetMapping("/music/{filename}")
    public ResponseEntity<StreamingResponseBody> downloadMusic(
            @PathVariable String filename,
            @RequestHeader HttpHeaders headers) {
        System.out.println("下载音乐"+filename);
        return huaweiStorageDownloadService.downloadFile("music", filename, headers);
    }
}