package com.qst.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.admin.mapper.MusicMapper;
import com.qst.admin.mapper.UserMapper;
import com.qst.domain.entity.Log;
import com.qst.admin.mapper.LogMapper;
import com.qst.admin.service.ILogService;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements ILogService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    MusicMapper musicMapper;
    @Override
    public Mess getLog(Integer pn, Integer size) {
        Page<Log> page = new Page<>(pn, size);
        QueryWrapper<Log> logQueryWrapper = new QueryWrapper<>();
        logQueryWrapper.orderByDesc("create_date");
        QueryWrapper<User> userWrapper = new QueryWrapper<User>();
        userWrapper.eq("role",1);
        Long singNumb=userMapper.selectCount(userWrapper);
        userWrapper=new QueryWrapper<>();
        userWrapper.eq("role",2);
        Long userNumb=userMapper.selectCount(userWrapper);
        page(page,logQueryWrapper);
        return Mess.success().data("logs",page).data("regSinger",singNumb).data("regUser",userNumb).data("musicNumb",musicMapper.selectCount(new QueryWrapper<>()));
    }
}
