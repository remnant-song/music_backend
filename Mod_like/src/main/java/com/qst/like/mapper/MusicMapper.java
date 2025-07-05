package com.qst.like.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qst.domain.entity.Music;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 歌曲信息表 Mapper 接口
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface MusicMapper extends BaseMapper<Music> {
    @Select("SELECT\n" +
            "music.*,\n" +
            "`user`.username\n" +
            "FROM\n" +
            "mylike ,\n" +
            "music ,\n" +
            "`user`\n" +
            "WHERE\n" +
            "mylike.music = music.music_id AND\n" +
            "music.from_singer = `user`.id AND\n" +
            "mylike.`user` = ${id}\n")
    List<Music> getLikeMusic(Integer id);
}
