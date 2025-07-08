package com.qst.songList.controller;

import com.qst.domain.entity.Mess;
import com.qst.domain.entity.SongList;
import com.qst.domain.util.JwtUtils;
import com.qst.songList.service.IListMusicService;
import com.qst.songList.service.ISongListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/songList")
public class SongListController {
    @Autowired
    ISongListService songListService;
    @Autowired
    IListMusicService listMusicService;
    @GetMapping("/search")
    public Mess search(String keyword){
        System.out.println(">>> 收到搜索 keyword: " + keyword);
        return songListService.search(keyword);
    }

    @GetMapping("/recommendList")
    public Mess recommendList(){
        return songListService.recommendList();
    }

    @GetMapping("/select")
    public Mess select(Integer listId, @RequestHeader(value = "id",required = false)Integer id){
        return songListService.getSongList(listId,id);
    }
    @PostMapping("/create")
    public Mess createList(@RequestBody SongList songList, @RequestHeader("Authorization") String token/*@RequestHeader("id")Integer id*/) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return songListService.createList(songList,id);
    }

    @GetMapping("/getMySongList")
    public Mess getMySongList(@RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return songListService.getMySongList(id);
    }

    @PostMapping("/addSong")
    public Mess addSong(Integer songId,Integer listId){
        return listMusicService.addSong(songId,listId);
    }
    @PostMapping("/removeSong")
    public Mess removeSong(Integer songId,Integer listId){
        return listMusicService.removeSong(songId,listId);
    }
    @PostMapping("/removeSongList")
    public Mess removeSongList(Integer listId,@RequestHeader("Authorization") String token/*@RequestHeader("id")Integer id*/) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return songListService.removeSongList(listId,id);
    }
    @PostMapping("/update")
    public Mess updateSongList(@RequestBody SongList songList ,@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return songListService.updateSongList(songList,id);
    }
}