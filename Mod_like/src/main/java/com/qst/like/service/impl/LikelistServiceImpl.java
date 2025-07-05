package com.qst.like.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.Likelist;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.SongList;
import com.qst.like.mapper.SongListMapper;
import com.qst.domain.util.RedisUtils;
import com.qst.like.mapper.LikelistMapper;
import com.qst.like.service.ILikelistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 用户-歌单收藏表 服务实现类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Service
public class LikelistServiceImpl extends ServiceImpl<LikelistMapper, Likelist> implements ILikelistService {
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    SongListMapper songListMapper;
    @Override
    public Mess getList(Integer id) {
        List<SongList> list=null;
        try{
            list= (List<SongList>) redisUtils.get("likeListID_".concat("id"));
            if(list==null||list.size()==0){
                list = songListMapper.getLikeSongList(id);
                redisUtils.set("likeListID_".concat("id"),list,15L, TimeUnit.MINUTES);
            }
        }catch (Exception e){
            list = songListMapper.getLikeSongList(id);
            redisUtils.set("likeListID_".concat(String.valueOf(id)),list);
        }
        return Mess.success().data("list",list);
    }

    @Override
    public Mess addList(Integer listId, Integer id) {
        QueryWrapper<Likelist> wrapper = new QueryWrapper<>();
        wrapper
                .eq("userId",id)
                .eq("listId",listId);
        if(count(wrapper)==0){
            Likelist likelist = new Likelist();
            likelist.setListId(listId);
            likelist.setUserId(id);
            save(likelist);
            redisUtils.remove("likeListID_".concat("id"));
            return Mess.success().mess("收藏成功");
        }
        return Mess.fail().mess("您已收藏");
    }

    @Override
    public Mess removeList(Integer listId, Integer id) {
        QueryWrapper<Likelist> wrapper = new QueryWrapper<>();
        wrapper
                .eq("userId",id)
                .eq("listId",listId);
        if(remove(wrapper)){
            redisUtils.remove("likeListID_".concat("id"));
            return Mess.success().mess("取消收藏");
        }else{
            return Mess.fail().mess("取消收藏失败");
        }
    }
}
