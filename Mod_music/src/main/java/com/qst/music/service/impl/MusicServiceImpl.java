package com.qst.music.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Music;
import com.qst.domain.util.RedisUtils;
import com.qst.music.mapper.MusicMapper;
import com.qst.music.service.IMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Import(value = RedisUtils.class)
@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements IMusicService {

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    MusicMapper mapper;
    @Override
    public Mess Recommend() {
        System.out.println("Recommend接口被调用了");
        Mess mess=null;
        mess = (Mess) redisUtils.get("Recommend");
        if (mess==null){
            mess=Mess.success().data("hotMusic",mapper.recommendHot(4)).data("newMusic",mapper.recommendNew(4)).data("recommendMusic",mapper.recommendRandom(4));
            redisUtils.set("Recommend",mess,15L, TimeUnit.MINUTES);
        }
        return mess;
    }

    @Override
    public Mess getRecommendList(String param) {
        Mess mess=null;
        switch (param){
            case "hotList":
                mess= (Mess) redisUtils.get("hotList");
                if(mess==null){
                    mess=Mess.success().data("list",mapper.recommendHot(10));
                    redisUtils.set("hotList",mess,15L, TimeUnit.MINUTES);
                }

                break;
            case "newList":
                mess= (Mess) redisUtils.get("newList");
                if(mess==null){
                    mess=Mess.success().data("list",mapper.recommendNew(10));
                    redisUtils.set("newList",mess,15L, TimeUnit.MINUTES);
                }
                break;
            case "recommendList":
                mess= (Mess) redisUtils.get("recommendList");
                if(mess==null){
                    mess=Mess.success().data("list",mapper.recommendRandom(10));
                    redisUtils.set("recommendList",mess,15L, TimeUnit.MINUTES);
                }
                break;
            default:
                mess=Mess.fail().mess("查询参数错误");
        }
        return mess;
    }

    @Override
    public Mess search(String keyword) {
        keyword=keyword==null?"":keyword;
        List<Music> list= (List<Music>) redisUtils.get("Search_".concat(keyword));
        if(list==null||list.size()==0){
            list = mapper.search(keyword);
            redisUtils.set("Search_".concat(keyword),list,15L,TimeUnit.MINUTES);
        }
        return Mess.success().data("list",list);
    }
    @Override
    public Mess getMusic(Integer musicId,Integer id) {
        Music music= mapper.getMusic(musicId);
//        Music music= mapper.getMusic(5);
//        System.out.println("getMusic");
        music= (Music) redisUtils.get("musicID_".concat(String.valueOf(musicId)));
        if (music==null){
            System.out.println("redis中musicId为空");
            music = mapper.getMusic(musicId);
            redisUtils.set("musicID_".concat(String.valueOf(musicId)),music,15L,TimeUnit.MINUTES);
        }
        if (music==null){
            System.out.println("歌曲不存在或已被冻结");
            return Mess.fail().mess("歌曲不存在或已被冻结");
        }else{
            addNumb(musicId);
            if(id!=null){
                List<Music> myLike = getMyLike(id);
                for (Music music1 : myLike) {
                    if (music1.getMusicId().equals(musicId)){
                        music.setIsLike(1);
                        break;
                    }
                }
            }
            System.out.println(music.toString());
            return Mess.success().data("music",music);
        }
    }
    public void  addNumb(Integer id){
        try {
            Integer numb= (Integer) redisUtils.get("numb_".concat(String.valueOf(id)));
            if (numb==null)numb=1;
            else if(numb==9){
                UpdateWrapper<Music> wrapper = new UpdateWrapper<>();
                wrapper.eq("music_id",id).setSql("listen_numb = listen_numb+10");
                update(wrapper);
                numb=0;
            }else {
                numb++;
            }
            redisUtils.set("numb_".concat(String.valueOf(id)),numb);
        }catch (Exception e){
            System.out.println(e.getMessage());

        }
    }
    public List<Music> getMyLike(Integer id){
        List<Music> myLikeList=null;
        try{
            myLikeList=(List<Music>) redisUtils.get("myLikeID_".concat(String.valueOf(id)));
            if(myLikeList==null||myLikeList.size()==0){
                myLikeList=mapper.getLikeMusic(id);
                redisUtils.set("myLikeID_".concat(String.valueOf(id)),myLikeList);
            }
        }catch (Exception e){
            myLikeList=mapper.getLikeMusic(id);
            redisUtils.set("myLikeID_".concat(String.valueOf(id)),myLikeList);
        }
        return myLikeList;
    }
}