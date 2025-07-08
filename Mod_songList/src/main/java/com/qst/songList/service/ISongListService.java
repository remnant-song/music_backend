package com.qst.songList.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.SongList;

/**
 * <p>
 * 歌单信息表 服务类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface ISongListService extends IService<SongList> {
    Mess search(String keyword);

    Mess recommendList();

    Mess getSongList(Integer listId, Integer id);

    Mess createList(SongList songList, Integer id);

    Mess getMySongList(Integer id);

    Mess removeSongList(Integer listId, Integer id);

    Mess updateSongList(SongList songList, Integer id);
}
