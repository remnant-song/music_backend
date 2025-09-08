package com.qst.like.controller;

import com.qst.domain.entity.Mess;
import com.qst.domain.util.JwtUtils;
import com.qst.like.service.ILikelistService;
import com.qst.like.service.IMusicService;
import com.qst.like.service.IMylikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mylike")
public class LikeController {

    @Autowired
    IMusicService musicService;

    @Autowired
    ILikelistService likelistService;

    @Autowired
    IMylikeService mylikeService;

    @GetMapping("/getMyLike")
    public Mess getMylike(@RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        System.out.println("id="+id);
        return Mess.success().data("likeList",musicService.getLikeMusic(id));
    }

    @GetMapping("/getLikeList")
    public Mess getLikeList(@RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return likelistService.getList(id);
    }

    @PostMapping("/add")
    public Mess addLike(@RequestHeader("Authorization") String token,Integer musicId){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return mylikeService.addLike(musicId,id);
    }
    @PostMapping("/remove")
    public Mess remove(@RequestHeader("Authorization") String token,Integer musicId){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return mylikeService.removeLike(musicId,id);
    }

    @PostMapping("/addList")
    public Mess addList(@RequestHeader("Authorization") String token,@RequestParam(value = "listId") Integer listId){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return likelistService.addList(listId,id);
    }

    @PostMapping("/removeList")
    public Mess removeList(@RequestHeader("Authorization") String token,Integer listId){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return likelistService.removeList(listId,id);
    }
}