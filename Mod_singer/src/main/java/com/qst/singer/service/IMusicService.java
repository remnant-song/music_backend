package com.qst.singer.service;

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
    Mess getMusic(String keyword, Integer id, Integer pn, Integer size);

    Mess addMusic(Music music, Integer id);

    Mess updateMusic(Music music, Integer id);

    Mess deleteMusic(Integer musicId, Integer id);

    Mess getMessage(Integer id);
}
