package com.qst.domain.service.impl;

import com.qst.domain.entity.Msg;
import com.qst.domain.mapper.MsgMapper;
import com.qst.domain.service.IMsgService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
