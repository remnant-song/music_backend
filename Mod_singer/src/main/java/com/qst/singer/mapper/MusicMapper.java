package com.qst.singer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qst.domain.entity.Music;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
            "user.username\n" +
            "FROM\n" +
            "user ,\n" +
            "music\n" +
            "WHERE\n" +
            "user.id = music.from_singer AND\n" +
            "music.music_name LIKE '%${Search}%' AND\n"+
            "music.from_singer = ${id}")
    IPage<Music> searchMusic(Page page, @Param(value ="Search" ) String Search, @Param(value = "id")Integer id);
    @Select("SELECT\n" +
            "Sum(music.listen_numb)\n" +
            "FROM\n" +
            "music\n" +
            "WHERE\n" +
            "music.from_singer = ${id}")
    Integer getMusicNumb(@Param(value = "id")Integer id);
}
