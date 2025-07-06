package com.qst.upload;

import com.qst.domain.entity.Log;
import com.qst.upload.huawei.HuaweiStorageUploadService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;
//
//@Service
//public class UploadService {
//
//    @Value("${download.address}")
//    String address;
//
//    @Value("${service.ipAddr}")
//    String ip;
//
//    @Value("${server.port}")
//    String port;
//
//    public String uploadImage(MultipartFile file){
//        return upLoadFile(file,"image");
//    }
//    public String uploadLyric(MultipartFile file){
//        return upLoadFile(file,"lyric");
//    }
//    public String uploadMusic(MultipartFile file){
//        return upLoadFile(file,"music");
//    }
//
//    public String upLoadFile(MultipartFile file,String type){
//        OutputStream os = null;
//        InputStream is = null;
//        String newName=getNewName(file);
//        try {
//            is=file.getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            byte[] bs=new byte[1024];
//            int length;
//            File tempFile = new File(address.concat(type));
//            if (!tempFile.exists()){
//                tempFile.mkdirs();
//            }
//            os = new FileOutputStream(tempFile.getPath().concat(File.separator).concat(newName));
//            while ((length = is.read(bs)) != -1) {
//                os.write(bs, 0, length);
//            }
//
//            return ip.concat(":").concat(port).concat("/file/").concat(type).concat("/").concat(newName);
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }finally {
//            try {
//                os.close();
//                is.close();
//            }catch (Exception e){
//
//            }
//        }
//        return null;
//    }
//
//    public String getNewName(MultipartFile file){
//        String uuid= UUID.randomUUID().toString();
//        String filename=file.getOriginalFilename();
//        String newName=uuid+filename.substring(filename.lastIndexOf("."));
//        return newName;
//    }
//
//}


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

//    public String upLoadFile(MultipartFile file, String type) {
//        try {
//            // 上传到华为云存储
//            String objectPath = type + "/" + getNewName(file);
//            String cloudUrl = huaweiStorageUploadService.uploadFile(file, objectPath);
//
//            // 返回可访问的URL（根据实际情况调整）
//            return cloudUrl;
//        } catch (Exception e) {
////            log.error("华为云文件上传失败", e);
//            throw new RuntimeException("文件上传失败", e);
//        }
//    }
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