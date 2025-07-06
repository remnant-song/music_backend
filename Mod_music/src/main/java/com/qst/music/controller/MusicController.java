package com.qst.music.controller;

import com.qst.domain.entity.Mess;
import com.qst.domain.util.JwtUtils;
import com.qst.music.service.IMusicService;
import com.qst.music.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/music")
public class MusicController {
    @Autowired
    IMusicService musicService;

    @Autowired
    IUserService userService;

    @GetMapping("/Recommend")
    public Mess getRecommend(){
        System.out.println("controller中Recommend接口被调用了");
        return musicService.Recommend();
    }

    @GetMapping("/RecommendList")
    public Mess getRecommendList(String param){
        return musicService.getRecommendList(param);
    }

    @GetMapping("/search")
    public Mess search(String keyword){
        return musicService.search(keyword);
    }

    @GetMapping("/detail")
    public Mess getOne(@RequestParam Integer musicId, @RequestHeader("Authorization") String token){

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        System.out.println("controller中detail接口被调用"+"musicId="+musicId+"id="+id);
        return musicService.getMusic(musicId,id);
    }
    @GetMapping("/getRecommendSinger")
    public Mess getRecommendSinger(){
        return userService.recommendSinger();
    }

    @GetMapping("/singerDetails")
    public Mess singerDetails(Integer id){
        return userService.singerDetails(id);
    }
}