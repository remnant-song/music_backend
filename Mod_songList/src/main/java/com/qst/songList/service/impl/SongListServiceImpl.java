package com.qst.songList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.*;
import com.qst.domain.mapper.LogMapper;
import com.qst.songList.mapper.SongListMapper;
import com.qst.songList.service.ISongListService;
import com.qst.domain.util.RedisUtils;
import com.qst.songList.mapper.LikelistMapper;
import com.qst.songList.mapper.ListMusicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 歌单信息表 服务实现类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Import(value = RedisUtils.class)
@Service
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements ISongListService {
    @Autowired
    ListMusicMapper listMusicMapper;
    @Autowired
    SongListMapper songListMapper;
    @Autowired
    LikelistMapper likelistMapper;
    @Autowired
    RedisUtils redisUtils;

    // "likeListID_".concat("id")
    public List<SongList> getLikeList(Integer id) {
        List<SongList> list=null;
        try{
            list= (List<SongList>) redisUtils.get("likeListID_".concat("id"));
            if(list==null||list.size()==0){
                list = songListMapper.getLikeSongList(id);
                redisUtils.set("likeListID_".concat("id"),list,15L, TimeUnit.MINUTES);
            }
        }catch (Exception e){
            e.getMessage();
            list = songListMapper.getLikeSongList(id);
            redisUtils.set("likeListID_".concat(String.valueOf(id)),list,15L, TimeUnit.MINUTES);
        }
        return list;
    }
    @Override
    public Mess getSongList(Integer listId, Integer id) {
        SongList songList = songListMapper.getListById(listId);
        if (songList==null){
            return Mess.fail().mess("此歌单已被删除");
        }
        if(id!=null){
            List<SongList> list = getLikeList(id);
            for (SongList songList1 : list) {
                if(songList1.getId().equals(listId)){
                    songList.setIsLike(1);
                }
            }
        }
        List<Music> musicList = listMusicMapper.getMusicListByListId(listId);
        return Mess.success().data("listMessage",songList).data("musicList",musicList);
    }

    @Override
    public Mess createList(SongList songList,Integer id) {
        try {
            songList.setCreateDate(LocalDate.now());
            songList.setUser(id);
            return save(songList)?Mess.success().mess("创建成功"):Mess.fail().mess("创建失败");
        }catch (Exception e){
            return Mess.fail().mess(e.getMessage());
        }
    }

    @Override
    public Mess search(String keyword) {
        List<SongList> search = songListMapper.search(keyword);

        return Mess.success().data("songList",search);
    }

    @Override
    public Mess recommendList() {
        List<SongList> lists = songListMapper.recommendList();
        return Mess.success().data("list",lists);
    }

    @Override
    public Mess getMySongList(Integer id) {
        QueryWrapper<SongList> wrapper = new QueryWrapper<>();
        wrapper.eq("user",id);
        List<SongList> lists = list(wrapper);
        return Mess.success().data("list",lists);
    }

    @Override
    public Mess removeSongList(Integer listId, Integer id) {
        QueryWrapper<ListMusic> listMusicQueryWrapper = new QueryWrapper<>();
        listMusicQueryWrapper.eq("listid",listId);
        listMusicMapper.delete(listMusicQueryWrapper);
        QueryWrapper<SongList> wrapper = new QueryWrapper<>();
        wrapper
                .eq("user",id)
                .eq("id",listId);
        if(remove(wrapper)){

            return Mess.success().mess("删除成功");
        }
        return Mess.fail().mess("删除失败");
    }

    @Override
    public Mess updateSongList(SongList songList, Integer id) {
        updateById(songList);
        Log log = new Log();
        System.out.println("ID为 ".concat(id.toString().concat("的用户")));
        System.out.println("更新歌曲信息 歌曲名为：");
        System.out.println(songList.getName());
        System.out.println(LocalDate.now());
        redisUtils.remove("musicID_".concat(String.valueOf(songList.getId())));
        return Mess.success().mess("修改成功");
    }
}
