package com.qst.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.Log;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.User;
import com.qst.domain.util.CheckUtil;
import com.qst.domain.util.JwtUtils;
import com.qst.domain.util.MD5Utils;
import com.qst.login.mapper.LogMapper;
import com.qst.login.mapper.UserMapper;
import com.qst.login.service.IUserService;
import com.qst.login.util.SendMess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;

import static io.lettuce.core.LettuceVersion.getName;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *此登录方法首先判断登录输入的用户名为什么格式（可以通过手机号或邮箱快速登录）
 * 并查询是否有对应的账号，如果没有查询到账号则返回错误信息，
 * 如果该账号激活状态为1则表示该账号已冻结，返回错误信息“用户已冻结”。
 * 查询到账号后再对比用户输入密码是否正确，
 * 如果输入密码的MD5码与不一致则返回密码错误，
 * 一致则通过getJwtToken方法生成Token，
 * 将密码设置为null并将token以及用户信息返回给前端。
 * @author hbr
 * @since 2025-06-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    LogMapper logMapper;
    @Autowired
    SendMess sendMess;


    static final String charStr = "0123456789abcdefghijklmnopqrstuvwxyz";

    public  String getCharAndNumr() {
        Random random = new Random();
        StringBuffer valSb = new StringBuffer();
        int charLength = charStr.length();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(charLength);
            valSb.append(charStr.charAt(index));
        }
        return valSb.toString();
    }
    public  String  getName(){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        String name = getCharAndNumr();
        wrapper.like("username",name.concat("%"));
        name=name.concat("#").concat(String.valueOf(count(wrapper)+1));
        return name;
    }
    @Override
    public Mess register(User user) {
        System.out.println("userServiceImpl.java.register()");
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        wrapper.eq("email",user.getEmail()).or().eq("phone",user.getPhone());
        if(getOne(wrapper)!=null){
            return Mess.fail().mess("邮箱或手机号已被注册");
        }else if(user.getRole()>2||user.getRole()<1){
            return Mess.fail().mess("注册失败 请选择正确角色");
        }
        else {
            user.setUsername(getName());
            user.setCreateTime(LocalDate.now());
            user.setPassword(MD5Utils.stringToMD5(user.getPassword()));
            user.setActivation(1);
            save(user);
            Log log = new Log();
            log.setCreateDate(LocalDate.now());
            log.setDoSome("注册");
            log.setUserName(user.getUsername());
            logMapper.insert(log);
            sendMess.SendActivation(user,user.getEmail());
            return Mess.success().mess("注册成功");
        }
    }

    @Override
    public Mess login(User user) {
        System.out.println("login");
        QueryWrapper<User> wrapper=new QueryWrapper<>();
        if (CheckUtil.checkPhone(user.getUsername())){
            wrapper.eq("phone",user.getUsername());
        }else  if(CheckUtil.isEmail(user.getUsername())){
            wrapper.eq("email",user.getUsername());
        }else{
            wrapper.eq("username",user.getUsername());
        }
        User one = getOne(wrapper);
        if (one==null)return Mess.fail().mess("用户不存在");
        else if(one.getActivation()==1)return Mess.fail().mess("用户已冻结");
        else if(MD5Utils.stringToMD5(user.getPassword()).equals(one.getPassword())){
            String token = JwtUtils.getJwtToken(one.getId(), one.getRole(),one.getUsername());
            one.setPassword(null);
            return Mess.success().data("token",token).data("user",one);
        }else {
            return Mess.fail().mess("密码错误");
        }
    }

    @Override
    public Mess getInfo(Integer id) {
        User user = getById(id);
        return Mess.success().data("user",user);
    }

    @Override
    public String activation(String token) {
        try {
            String name = JwtUtils.getMemberNameByJwtToken(token);
            UpdateWrapper<User> wrapper = new UpdateWrapper<>();
            wrapper.eq("username",name).set("activation",0);
            update(wrapper);
            return "账号激活成功";
        }catch (Exception e){
            e.printStackTrace();  // 打印堆栈，方便排查
            System.out.println(e);
            return "账号激活失败 失败信息为".concat(e.getMessage());
        }
    }
}
