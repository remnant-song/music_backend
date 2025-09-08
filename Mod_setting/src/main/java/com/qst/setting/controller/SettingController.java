package com.qst.setting.controller;

import com.qst.domain.entity.Mess;
import com.qst.domain.entity.User;
import com.qst.domain.util.JwtUtils;
import com.qst.setting.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setting")
public class SettingController {

    @Autowired
    IUserService userService;

    @PostMapping("/setMessage")
    public Mess setAbout(@RequestBody User user, @RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return userService.setMessage(user,id);
    }

    @PostMapping("/setPassword")
    public Mess setPassword(String oldPassword,String newPassword,@RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return userService.setPassword(oldPassword,newPassword,id);
    }

    @PostMapping("/setIcon")
    public Mess setIcon(@RequestHeader("Authorization") String token,String iconUrl){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        System.out.println("settingController.setIcon");
        return userService.setIcon(iconUrl,id);
    }
}