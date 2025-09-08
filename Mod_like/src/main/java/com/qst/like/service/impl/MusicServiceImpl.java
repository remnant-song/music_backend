package com.qst.like.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.Music;
import com.qst.domain.util.RedisUtils;
import com.qst.like.mapper.MusicMapper;
import com.qst.like.service.IMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 歌曲信息表 服务实现类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Import(value = RedisUtils.class)
@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements IMusicService {
    @Autowired
    MusicMapper musicMapper;
    @Autowired
    RedisUtils redisUtils;

    @Override
    public List<Music> getLikeMusic(Integer id) {
        List<Music> myLikeList=null;
        try{
            myLikeList=(List<Music>) redisUtils.get("myLikeID_".concat(String.valueOf(id)));
            if(myLikeList==null||myLikeList.size()==0){
                myLikeList=musicMapper.getLikeMusic(id);
                redisUtils.set("myLikeID_".concat(String.valueOf(id)),myLikeList,15L, TimeUnit.MINUTES);
            }
        }catch (Exception e){
            myLikeList=musicMapper.getLikeMusic(id);
            redisUtils.set("myLikeID_".concat(String.valueOf(id)),myLikeList,15L, TimeUnit.MINUTES);
        }
        return myLikeList;
    }
}
