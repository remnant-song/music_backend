package com.qst.upload.util;

public class CheckUtil {

    public static Boolean isImage(String filename){
        String type=filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return type.equals(".jpg")||type.equals(".jpeg")||type.equals(".png")||type.equals(".gif");
    }
    public static Boolean isLyric(String filename){
        String type=filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return type.equals(".lrc")||type.equals(".LRC");
    }
    public static Boolean isMusic(String filename){
        String type=filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return type.equals(".mp3")||type.equals(".wma")||type.equals(".wav")||type.equals(".ogg");
    }
}