package com.qst.msg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Msg;

/**
 * <p>
 * 用户消息表 服务类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface IMsgService extends IService<Msg> {
    Mess addMsg(String title, String message, String togroup, String towhose);

    Mess getMsg(Integer id);

    Mess getCount(Integer id);

    Mess readMsg(Integer id);
}
