package com.qst.setting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.User;
import com.qst.domain.util.MD5Utils;
import com.qst.setting.mapper.UserMapper;
import com.qst.setting.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Mess setMessage(User user, Integer id) {
        try {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper
                    .eq("username",user.getUsername())
                    .or().eq("email",user.getEmail())
                    .or().eq("phone",user.getPhone());
            User user1 = getOne(wrapper);
            if(user1==null||user1.getId().equals(id)){
                user.setId(id);
                updateById(user);
                return Mess.success().mess("更新成功");
            }else if(user1.getUsername().equals(user.getUsername())){
                return Mess.fail().mess("用户名已存在");
            }else if(user1.getEmail().equals(user.getEmail())){
                return Mess.fail().mess("邮箱已存在");
            }else if(user1.getPhone().equals(user.getPhone())){
                return Mess.fail().mess("手机号已存在");
            }else {
                return Mess.fail().mess("其他错误");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return Mess.fail().mess("用户信息已存在");
        }
    }

    @Override
    public Mess setPassword(String oldPassword, String newPassword, Integer id) {
        User user = getById(id);
        String odpassword= MD5Utils.stringToMD5(oldPassword);
        if(user.getPassword().equals(odpassword)){
            user.setPassword(MD5Utils.stringToMD5(newPassword));
            updateById(user);
            return Mess.success().mess("修改成功");
        }
        return Mess.fail().mess("密码错误");
    }

    @Override
    public Mess setIcon(String iconUrl, Integer id) {
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",id).set("image_url",iconUrl);
        return update(wrapper)?Mess.success().mess("更新成功"):Mess.fail().mess("更新失败");
    }
}
