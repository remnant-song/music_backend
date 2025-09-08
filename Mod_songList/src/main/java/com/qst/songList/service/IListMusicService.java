package com.qst.songList.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qst.domain.entity.ListMusic;
import com.qst.domain.entity.Mess;

/**
 * <p>
 * 歌单-歌曲关系表 服务类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface IListMusicService extends IService<ListMusic> {
    Mess addSong(Integer songId, Integer listId);

    Mess removeSong(Integer songId, Integer listId);
}
