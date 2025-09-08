package com.qst.domain.service.impl;

import com.qst.domain.entity.Log;
import com.qst.domain.mapper.LogMapper;
import com.qst.domain.service.ILogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
