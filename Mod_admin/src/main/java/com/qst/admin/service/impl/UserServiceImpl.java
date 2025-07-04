package com.qst.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.admin.mapper.MusicMapper;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Music;
import com.qst.domain.entity.User;
import com.qst.admin.mapper.UserMapper;
import com.qst.admin.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    MusicMapper musicMapper;

    @Override
    public Mess searchUser(Integer pn, Integer size, String keyword) {
        Page<User> userPage = new Page<>(pn, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper
                .and(e->{
                    e
                            .like("username",keyword)
                            .or()
                            .like("id",keyword)
                            .or()
                            .like("phone",keyword)
                            .or()
                            .like("email",keyword);

                })
                .and(e->{
                    e.ne("role",0);
                });
        Page<User> page = page(userPage, wrapper);
        return Mess.success().data("page",page);
    }

    @Override
    public Mess freezeUser(Integer userId) {
        UpdateWrapper<User> userWrapper=new UpdateWrapper<>();
        UpdateWrapper<Music> musicWrapper=new UpdateWrapper<>();
        userWrapper.eq("id",userId).setSql("activation = 1");
        update(userWrapper);
        musicWrapper.eq("from_singer",userId).setSql("activation = 2");
        musicMapper.update(null,musicWrapper);
        return Mess.success().mess("冻结成功");
    }
    @Override
    public Mess unFreezeUser(Integer userId) {
        UpdateWrapper<User> userWrapper=new UpdateWrapper<>();
        UpdateWrapper<Music> musicWrapper=new UpdateWrapper<>();
        userWrapper.eq("id",userId).setSql("activation = 0");
        update(userWrapper);
        musicWrapper.eq("from_singer",userId).setSql("activation = 0");
        musicMapper.update(null,musicWrapper);
        return Mess.success().mess("解冻成功");
    }

}
