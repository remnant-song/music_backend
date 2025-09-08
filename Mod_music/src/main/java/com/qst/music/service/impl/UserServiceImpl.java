package com.qst.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Music;
import com.qst.domain.entity.User;
import com.qst.music.mapper.MusicMapper;
import com.qst.music.mapper.UserMapper;
import com.qst.music.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Mess recommendSinger() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper
                .eq("role",1)
                .eq("activation",0)
                .orderByAsc("RAND()")
                .last("LIMIT 10");
        List<User> list =list(wrapper);
        return Mess.success().data("list",list);
    }

    @Override
    public Mess singerDetails(Integer id) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper
                .eq("role",1)
                .eq("activation",0)
                .eq("id",id);
        User singer = getOne(userQueryWrapper);
        if(singer==null)return Mess.fail().mess("歌手不存在或者已被冻结");
        QueryWrapper<Music> wrapper = new QueryWrapper<>();
        wrapper.eq("from_singer",id);
        List<Music> musicList = musicMapper.selectList(wrapper);
        return Mess.success().data("singer",singer).data("list",musicList);
    }
}
