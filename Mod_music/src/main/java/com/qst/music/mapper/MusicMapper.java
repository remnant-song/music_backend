package com.qst.music.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qst.domain.entity.Music;
import org.apache.ibatis.annotations.Param;
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
            "user.username\n" +
            "FROM\n" +
            "music ,\n" +
            "user\n" +
            "WHERE\n" +
            "music.from_singer = user.id AND\n" +
            "music.activation = 0 \n" +
            "ORDER BY\n" +
            "RAND()\n" +
            "LIMIT ${size}\n")
    public List<Music> recommendRandom(@Param(value = "size")Integer size);
    @Select("SELECT\n" +
            "music.*,\n" +
            "user.username\n" +
            "FROM\n" +
            "music ,\n" +
            "user\n" +
            "WHERE\n" +
            "music.from_singer = user.id AND\n" +
            "music.activation = 0 \n" +
            "ORDER BY\n" +
            "music.create_time DESC\n" +
            "LIMIT ${size}\n")
    public List<Music> recommendNew(@Param(value = "size")Integer size);
    @Select("SELECT\n" +
            "music.*,\n" +
            "user.username\n" +
            "FROM\n" +
            "music ,\n" +
            "user\n" +
            "WHERE\n" +
            "music.from_singer = user.id AND\n" +
            "music.activation = 0 \n" +
            "ORDER BY\n" +
            "music.listen_numb DESC\n" +
            "LIMIT ${size}\n")
    public List<Music> recommendHot(@Param(value = "size")Integer size);

    @Select("SELECT\n" +
            "music.*,\n" +
            "user.username\n" +
            "FROM\n" +
            "music ,\n" +
            "user\n" +
            "WHERE\n" +
            "music.from_singer = user.id AND\n" +
            "music.activation = 0 AND\n" +
            "(music.music_name LIKE '%${keyword}%' OR\n" +
            "user.username LIKE '%${keyword}%' )\n"+
            "ORDER BY\n" +
            "music.listen_numb DESC\n" )
    public List<Music> search(@Param(value = "keyword")String keyword);

    @Select("SELECT\n" +
            "music.*,\n" +
            "user.username\n" +
            "FROM\n" +
            "music ,\n" +
            "user\n" +
            "WHERE\n" +
            "music.from_singer = user.id AND\n" +
            "music.activation = 0 AND\n" +
            "music.music_id = ${id} \n")
    public Music getMusic(@Param(value = "id") Integer id);

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
