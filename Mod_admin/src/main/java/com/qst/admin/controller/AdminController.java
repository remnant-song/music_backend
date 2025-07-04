package com.qst.admin.controller;

import com.qst.admin.service.ILogService;
import com.qst.admin.service.IMusicService;
import com.qst.admin.service.IUserService;
import com.qst.domain.entity.Mess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    IUserService userService;
    @Autowired
    IMusicService musicService;
    @Autowired
    ILogService logService;

    @GetMapping(value = {"pageUser/{pn}/{size}"})
    public Mess pageUser(@PathVariable(value = "pn")Integer pn, @PathVariable(value = "size")Integer size, String keyword){
        return userService.searchUser(pn,size,keyword);
    }

    @PostMapping("/freezeUser")
    public Mess FreezeUser(Integer id){
        return userService.freezeUser(id);
    }

    @PostMapping("/unfreezeUser")
    public Mess unFreezeUser(Integer id){
        return userService.unFreezeUser(id);
    }

    @GetMapping("/pageMusic/{pn}/{size}")
    public Mess pageSearchMusic(@PathVariable(value = "pn")Integer pn, @PathVariable(value = "size")Integer size,String keyword){
        return musicService.searchMusic(pn,size,keyword);
    }
    @PostMapping("/freezeMusic")
    public Mess FreezeMusic(Integer id){
        return musicService.freezeMusic(id);
    }
    @PostMapping("/unfreezeMusic")
    public Mess unFreezeMusic(Integer id){
        return musicService.unFreezeMusic(id);
    }

    @GetMapping("/getLog/{pn}/{size}")
    public Mess getLog(@PathVariable("pn")Integer pn,@PathVariable("size")Integer size){
        return logService.getLog(pn,size);
    }
}