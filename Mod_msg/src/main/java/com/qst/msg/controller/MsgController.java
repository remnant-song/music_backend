package com.qst.msg.controller;

import com.qst.domain.entity.Mess;
import com.qst.domain.util.JwtUtils;
import com.qst.msg.service.IMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/msg")
public class MsgController {

    @Autowired
    IMsgService msgService;

    @PostMapping("/addMsg")
    public Mess addMsg(String title, String message, @RequestParam(required = false) String togroup, @RequestParam(required = false) String towhose){
        return msgService.addMsg(title,message,togroup,towhose);
    }

    @GetMapping("/getMsg")
    public Mess getMsg(@RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return msgService.getMsg(id);
    }

    @GetMapping("/getCount")
    public Mess getCount(@RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return msgService.getCount(id);
    }

    @PostMapping("/readMsg")
    public Mess readMsg(@RequestHeader("Authorization") String token){
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Integer id = JwtUtils.getMemberIdByJwtToken(token);
        if (id == null) {
            return Mess.fail().mess("无效或过期的 token");
        }
        return msgService.readMsg(id);
    }
}
