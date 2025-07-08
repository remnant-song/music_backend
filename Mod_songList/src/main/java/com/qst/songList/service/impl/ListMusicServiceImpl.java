package com.qst.songList.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.ListMusic;
import com.qst.domain.entity.Mess;
import com.qst.songList.mapper.ListMusicMapper;
import com.qst.songList.service.IListMusicService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌单-歌曲关系表 服务实现类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Service
public class ListMusicServiceImpl extends ServiceImpl<ListMusicMapper, ListMusic> implements IListMusicService {
    @Override
    public Mess addSong(Integer songId, Integer listId) {
        try {
            QueryWrapper<ListMusic> wrapper = new QueryWrapper<>();
            wrapper
                    .eq("music",songId)
                    .eq("listid",listId);
            if(count(wrapper)==0){
                ListMusic listMusic = new ListMusic();
                listMusic.setListid(listId);
                listMusic.setMusic(songId);
                save(listMusic);
                return Mess.success().mess("添加成功");
            }else{
                return Mess.fail().mess("添加失败");
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
            return Mess.fail().mess("添加错误");
        }
    }

    @Override
    public Mess removeSong(Integer songId, Integer listId) {
        try {
            QueryWrapper<ListMusic> wrapper = new QueryWrapper<>();
            wrapper
                    .eq("music",songId)
                    .eq("listid",listId);

            return remove(wrapper)?Mess.success().mess("移除成功"):Mess.fail().mess("移除错误");
        }catch (Exception e){
            System.out.println(e.getMessage());
            return Mess.fail().mess("移除错误");
        }
    }

}
