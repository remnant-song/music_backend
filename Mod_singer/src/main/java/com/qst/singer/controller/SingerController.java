package com.qst.singer.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Music;
import com.qst.domain.util.JwtUtils;
import com.qst.singer.service.IMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/singer")
public class SingerController {
    @Autowired
    IMusicService musicService;

    @GetMapping("/pageMusic/{pn}/{size}")
//    同样补充 token 判空、异常处理，避免直接抛 500
    public Mess getPage(@PathVariable(value = "pn") Integer pn, @PathVariable(value = "size") Integer size, String keyword, @RequestHeader(value = "Authorization", required = false) String token) {
        if (!StringUtils.hasLength(token)) {
            return Mess.fail().mess("未提供token");
        }
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id;
        try {
            id = JwtUtils.getMemberIdByJwtToken(token);
        } catch (Exception e) {
            return Mess.fail().mess("token无效或已过期");
        }
        return musicService.getMusic(keyword, id, pn, size);
    }


    @PostMapping("/addMusic")
    public Mess addMusic(@RequestBody Music music, @RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return musicService.addMusic(music,id);
    }

    @PostMapping("/update")
    public Mess update(@RequestBody Music music,@RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        System.out.println("music.imageUrl="+music.getImageUrl());
        return musicService.updateMusic(music,id);
    }

    @PostMapping("/delete")
    public Mess delete(Integer musicId ,@RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return musicService.deleteMusic(musicId,id);
    }

    @GetMapping("/getMess")
    public Mess getMess(@RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return musicService.getMessage(id);
    }
}