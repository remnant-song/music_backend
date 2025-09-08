package com.qst.like.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qst.domain.entity.Likelist;
import com.qst.domain.entity.Mess;

/**
 * <p>
 * 用户-歌单收藏表 服务类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface ILikelistService extends IService<Likelist> {
    Mess getList(Integer id);

    Mess addList(Integer listId, Integer id);

    Mess removeList(Integer listId, Integer id);
}
