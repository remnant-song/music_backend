package com.qst.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qst.domain.entity.Log;
import com.qst.domain.entity.Mess;

/**
 * <p>
 * 操作日志表 服务类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface ILogService extends IService<Log> {
    Mess getLog(Integer pn, Integer size);
}
