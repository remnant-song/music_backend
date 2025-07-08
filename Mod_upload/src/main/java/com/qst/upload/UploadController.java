package com.qst.upload;

import com.qst.domain.entity.Mess;
import com.qst.upload.huawei.HuaweiStorageUploadService;
import com.qst.upload.util.CheckUtil;
import com.qst.upload.util.MusicUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    UploadService uploadService;
    @PostMapping("upImage")
    public Mess upLoadImage(MultipartFile file){
        System.out.println("upImage");
        if(CheckUtil.isImage(file.getOriginalFilename())) {
            String url = uploadService.uploadImage(file);
            return Mess.success().mess("文件上传成功").data("url", url);
        }
        return Mess.fail().mess("文件格式错误");
    }

    @PostMapping("upLyric")
    public Mess upLoadLyric(MultipartFile file){
        if(CheckUtil.isLyric(file.getOriginalFilename())) {
            String url = uploadService.uploadLyric(file);
            return Mess.success().mess("文件上传成功").data("url", url);
        }
        return Mess.fail().mess("文件格式错误");
    }

    @PostMapping("upMusic")
    public Mess upLoadMusic(MultipartFile file){
        if (CheckUtil.isMusic(file.getOriginalFilename())){
            Integer length= MusicUtil.getDuration(file);
            String url=uploadService.uploadMusic(file);
            return Mess.success().mess("上传成功").data("url",url).data("timelength",length);
        }
        return Mess.fail().mess("文件格式错误");
    }

}