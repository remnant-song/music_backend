package com.qst.login.controller;

import com.qst.domain.entity.Mess;
import com.qst.domain.entity.User;
import com.qst.login.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    IUserService userService;

    @PostMapping("/register")
    public Mess Register(@RequestBody User user){
        System.out.println("register");
        return userService.register(user);
    }
    @GetMapping("/activation")
    public String getActivation(String token){
        return userService.activation(token);
    }


    @PostMapping("/login")
    public Mess Login(@RequestBody User user){
        System.out.println("login");
        return userService.login(user);
    }
    @GetMapping("/getInfo")
    public Mess getInfo( @RequestHeader("id")Integer id){
        return userService.getInfo(id);
    }
}