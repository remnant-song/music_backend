package com.qst.like.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qst.domain.entity.SongList;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 歌单信息表 Mapper 接口
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
public interface SongListMapper extends BaseMapper<SongList> {
    @Select("SELECT\n" +
            "song_list.*,\n" +
            "`user`.username\n" +
            "FROM\n" +
            "likelist ,\n" +
            "song_list,\n" +
            "`user`\n" +
            "WHERE\n" +
            "likelist.listId = song_list.id AND\n" +
            "song_list.`user` = `user`.id AND\n" +
            "likelist.userId = ${id}")
    public List<SongList> getLikeSongList(@Param(value = "id")Integer id);
}
