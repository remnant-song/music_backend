package com.qst.upload.util;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MusicUtil {
    public static Integer getDuration(File mp3File) {

        try {
            MP3File f = (MP3File) AudioFileIO.read(mp3File);
            MP3AudioHeader audioHeader = (MP3AudioHeader)f.getAudioHeader();
            return audioHeader.getTrackLength();
        } catch(Exception e) {
            System.out.println(e);
            return -1;
        }
    }

    public static Integer getDuration(MultipartFile file){
        File toFile = null;
        try{
            if (file.getSize() > 0) {
                InputStream ins = file.getInputStream();
                toFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
                inputStreamToFile(ins, toFile);
                ins.close();
            }
            return getDuration(toFile);
        }
        catch (Exception e){
            return -1;
        }finally {
            deleteTempFile(toFile);
        }
    }

    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteTempFile(File file) {
        CompletableFuture.runAsync(() -> {
            if (file != null) {
                File del = new File(file.toURI());
                boolean delete = del.delete();
                System.out.println(delete);
            }
        });
    }
}
