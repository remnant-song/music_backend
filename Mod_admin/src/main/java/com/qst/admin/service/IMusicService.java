package com.qst.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Music;

/**
 * <p>
 * 歌曲信息表 服务类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface IMusicService extends IService<Music> {
    Mess searchMusic(Integer pn, Integer size, String keyword);

    Mess freezeMusic(Integer id);

    Mess unFreezeMusic(Integer id);
}
