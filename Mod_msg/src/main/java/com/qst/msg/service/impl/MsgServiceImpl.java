package com.qst.msg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Msg;
import com.qst.domain.entity.User;
import com.qst.msg.mapper.UserMapper;
import com.qst.msg.mapper.MsgMapper;
import com.qst.msg.service.IMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 用户消息表 服务实现类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Service
public class MsgServiceImpl extends ServiceImpl<MsgMapper, Msg> implements IMsgService {
    @Autowired
    UserMapper userMapper;

    @Override
    public Mess addMsg(String title, String message, String togroup, String towhose) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if(togroup!=null&&togroup!=""){
            switch (togroup){
                case "role1":
                    wrapper.eq("role",1);
                    break;
                case "role2":
                    wrapper.eq("role",2);
                    break;
                default:
                    wrapper.eq("role",1).or().eq("role",2);
            }
            ArrayList<Msg> msgArrayList = new ArrayList<>();
            List<User> userList = userMapper.selectList(wrapper);
            userList.forEach(e->{
                Msg msg = new Msg();
                msg.setCreateTime(LocalDate.now());
                msg.setTitle(title);
                msg.setIsread(0);
                msg.setMsg(message);
                msg.setUserId(e.getId());
                msgArrayList.add(msg);
            });
            saveBatch(msgArrayList);
            return Mess.success().mess("发布成功");
        }else if(towhose ==null||!towhose.matches("-?\\d+(\\.\\d+)?")){
            return Mess.fail().mess("请发送正确的ID");

        }else{
            User user =userMapper.selectById(towhose);
            if (user==null){
                return Mess.fail().mess("该用户不存在");
            }
            else {
                Msg msg = new Msg();
                msg.setUserId(Integer.parseInt(towhose));
                msg.setMsg(message);
                msg.setTitle(title);
                msg.setIsread(0);
                msg.setCreateTime(LocalDate.now());
                save(msg);
                return Mess.success().mess("发布成功");
            }

        }
    }

    @Override
    public Mess getMsg(Integer id) {
        QueryWrapper<Msg> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",id);
        return Mess.success().mess("消息获取成功").data("msg",list(wrapper));
    }

    @Override
    public Mess getCount(Integer id) {
        QueryWrapper<Msg> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",id);
        wrapper.eq("isread",0);
        return Mess.success().data("unReadMsg",count(wrapper));
    }
    @Override
    public Mess readMsg(Integer id) {
        UpdateWrapper<Msg> wrapper = new UpdateWrapper<>();
        wrapper.set("isread",1).eq("user_id",id);
        update(wrapper);
        return Mess.success();
    }
}
