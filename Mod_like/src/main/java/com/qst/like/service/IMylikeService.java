package com.qst.like.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Mylike;

/**
 * <p>
 * 歌曲收藏表 服务类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface IMylikeService extends IService<Mylike> {
    Mess addLike(Integer musicId, Integer id);

    Mess removeLike(Integer musicId, Integer id);
}
