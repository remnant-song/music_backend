package com.qst.like.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qst.domain.entity.Music;

import java.util.List;

/**
 * <p>
 * 歌曲信息表 服务类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface IMusicService extends IService<Music> {
    List<Music> getLikeMusic(Integer id);
}
