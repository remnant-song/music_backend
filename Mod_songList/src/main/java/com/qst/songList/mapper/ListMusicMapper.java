package com.qst.songList.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qst.domain.entity.ListMusic;
import com.qst.domain.entity.Music;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 歌单-歌曲关系表 Mapper 接口
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface ListMusicMapper extends BaseMapper<ListMusic> {

    @Select("SELECT\n" +
            "user.username,\n" +
            "music.*\n" +
            "FROM\n" +
            "list_music ,\n" +
            "music ,\n" +
            "user \n" +
            "WHERE\n" +
            "list_music.music = music.music_id AND\n" +
            "music.from_singer = user.id AND\n" +
            "list_music.listid = ${id}")
    public List<Music> getMusicListByListId(@Param(value = "id")Integer id);
}
