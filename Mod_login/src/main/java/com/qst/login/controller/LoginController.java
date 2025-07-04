package com.qst.login.controller;

import com.alibaba.nacos.common.utils.StringUtils;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.User;
import com.qst.domain.util.JwtUtils;
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
        System.out.println("===激活接口被调用===");
        return userService.activation(token);
    }


    @PostMapping("/login")
    public Mess Login(@RequestBody User user){
        System.out.println("login");
        return userService.login(user);
    }
//    @GetMapping("/getInfo")
////    public Mess getInfo( @RequestHeader("id")Integer id){
//    public Mess getInfo(@RequestHeader("Authorization") String token) {
//        // 去掉"Bearer "前缀（如果你在Postman里用Bearer Token自动加了）
//        if (token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
//        Integer userId = JwtUtils.getMemberIdByJwtToken(token); // 你需要自己实现或确认这个方法
//        if (userId == null) {
//            return Mess.fail().mess("token无效或已过期");
//        }
//        return userService.getInfo(userId);
////        return userService.getInfo(id);
//    }

//    保证 token 判空（防止未传 Authorization 直接 NPE）；
//
//    保证 getMemberIdByJwtToken(...) 调用包裹在 try/catch 中防止异常中断请求。
@GetMapping("/getInfo")
public Mess getInfo(@RequestHeader(value = "Authorization", required = false) String token) {
    if (!StringUtils.hasLength(token)) {
        return Mess.fail().mess("未提供 token");
    }
    if (token.startsWith("Bearer ")) {
        token = token.substring(7);
    }
    Integer userId;
    try {
        userId = JwtUtils.getMemberIdByJwtToken(token);
    } catch (Exception e) {
        return Mess.fail().mess("token无效或已过期");
    }
    if (userId == null) {
        return Mess.fail().mess("token无效或已过期");
    }
    return userService.getInfo(userId);
}

}