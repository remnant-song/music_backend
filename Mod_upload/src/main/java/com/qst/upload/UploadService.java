package com.qst.upload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Service
public class UploadService {

    @Value("${download.address}")
    String address;

    @Value("${service.ipAddr}")
    String ip;

    @Value("${server.port}")
    String port;

    public String uploadImage(MultipartFile file){
        return upLoadFile(file,"image");
    }
    public String uploadLyric(MultipartFile file){
        return upLoadFile(file,"lyric");
    }
    public String uploadMusic(MultipartFile file){
        return upLoadFile(file,"music");
    }

    public String upLoadFile(MultipartFile file,String type){
        OutputStream os = null;
        InputStream is = null;
        String newName=getNewName(file);
        try {
            is=file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            byte[] bs=new byte[1024];
            int length;
            File tempFile = new File(address.concat(type));
            if (!tempFile.exists()){
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath().concat(File.separator).concat(newName));
            while ((length = is.read(bs)) != -1) {
                os.write(bs, 0, length);
            }

            return ip.concat(":").concat(port).concat("/file/").concat(type).concat("/").concat(newName);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            try {
                os.close();
                is.close();
            }catch (Exception e){

            }
        }
        return null;
    }

    public String getNewName(MultipartFile file){
        String uuid= UUID.randomUUID().toString();
        String filename=file.getOriginalFilename();
        String newName=uuid+filename.substring(filename.lastIndexOf("."));
        return newName;
    }

}