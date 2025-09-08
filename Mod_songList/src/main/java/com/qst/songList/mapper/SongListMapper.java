package com.qst.songList.mapper;

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
//    @Select("SELECT\n" +
//            "song_list.*,\n" +
//            "`user`.username\n" +
//            "FROM\n" +
//            "song_list ,\n" +
//            "`user`\n" +
//            "WHERE\n" +
//            "song_list.`user` = `user`.id AND\n" +
//            "song_list.`name` LIKE '%${keyword}%'")
@Select("SELECT song_list.*, `user`.username " +
        "FROM song_list, `user` " +
        "WHERE song_list.`user` = `user`.id " +
        "AND song_list.`name` LIKE CONCAT('%', #{keyword}, '%')")
    public List<SongList> search(@Param(value = "keyword") String keyword);
//    ${} → 直接字符串替换，不会做转义/绑定，容易出错（尤其是空值），而且存在 SQL 注入风险。
//    #{} → 预编译参数绑定，安全并能正确处理空值。
//    效果一致

    @Select("SELECT\n" +
            "song_list.*,\n" +
            "`user`.username\n" +
            "FROM\n" +
            "song_list ,\n" +
            "`user`\n" +
            "WHERE\n" +
            "song_list.`user` = `user`.id \n" +
            "ORDER BY RAND() " +
            "LIMIT 10")
    public List<SongList> recommendList();

    @Select("SELECT\n" +
            "song_list.*,\n" +
            "`user`.username\n" +
            "FROM\n" +
            "song_list ,\n" +
            "`user`\n" +
            "WHERE\n" +
            "song_list.`user` = `user`.id AND\n" +
            "song_list.id = ${listId}")
    SongList getListById(@Param(value = "listId") Integer listId);
    @Select("SELECT\n" +
            "song_list.*\n" +
            "FROM\n" +
            "song_list ,\n" +
            "likelist\n" +
            "WHERE\n" +
            "song_list.id = likelist.listId AND\n" +
            "likelist.userId = ${id}")
    public List<SongList> getLikeSongList(@Param(value = "id")Integer id);
}
