package com.qst.like.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Mylike;
import com.qst.domain.util.RedisUtils;
import com.qst.like.mapper.MylikeMapper;
import com.qst.like.service.IMylikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌曲收藏表 服务实现类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Service
public class MylikeServiceImpl extends ServiceImpl<MylikeMapper, Mylike> implements IMylikeService {
    @Autowired
    RedisUtils redisUtils;
    @Override
    public Mess addLike(Integer musicId, Integer id) {
        QueryWrapper<Mylike> wrapper = new QueryWrapper<>();
        wrapper
                .eq("user",id)
                .eq("music",musicId);
        if (count(wrapper)==0){
            Mylike mylike = new Mylike();
            mylike.setMusic(musicId);
            mylike.setUser(id);
            save(mylike);
            redisUtils.remove("myLikeID_".concat(String.valueOf(id)));
            return Mess.success().mess("添加成功");
        }else{
            return Mess.fail().mess("添加失败 您已喜欢此音乐");
        }

    }

    @Override
    public Mess removeLike(Integer musicId, Integer id) {
        QueryWrapper<Mylike> wrapper = new QueryWrapper<>();
        wrapper
                .eq("music",musicId)
                .eq("user",id);
        if(remove(wrapper)){
            redisUtils.remove("myLikeID_".concat(String.valueOf(id)));
            return Mess.success().mess("移除成功");
        }else{
            return Mess.fail().mess("移除失败");
        }
    }
}
