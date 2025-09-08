package com.qst.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface IUserService extends IService<User> {

    Mess searchUser(Integer pn, Integer size, String keyword);

    Mess freezeUser(Integer id);

    Mess unFreezeUser(Integer id);
}
