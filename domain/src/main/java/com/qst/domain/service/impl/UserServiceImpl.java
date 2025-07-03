package com.qst.domain.service.impl;

import com.qst.domain.entity.Mess;
import com.qst.domain.entity.User;
import com.qst.domain.mapper.UserMapper;
import com.qst.domain.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public Mess recommendSinger() {
        return null;
    }

    @Override
    public Mess singerDetails(Integer id) {
        return null;
    }
}
