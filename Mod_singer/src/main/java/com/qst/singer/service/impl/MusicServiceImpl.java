package com.qst.singer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.Log;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Music;
import com.qst.domain.util.RedisUtils;
import com.qst.singer.mapper.LogMapper;
import com.qst.singer.mapper.MusicMapper;
import com.qst.singer.service.IMusicService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
    LogMapper logMapper;

    @Autowired
    MusicMapper musicMapper;

    @Autowired
    RedisUtils redisUtils;

    @Override
    public Mess getMusic(String search, Integer id, Integer pn, Integer size) {
        Page<Music> page = new Page<>(pn,size);
        IPage<Music> musicIPage = musicMapper.searchMusic(page, search, id);
        return Mess.success().data("page",musicIPage);
    }

    @Override
    public Mess addMusic(Music music, Integer id) {
        music.setCreateTime(LocalDate.now());
        music.setFromSinger(id);
        List<String> tagList = music.getTagList();
        music.setTags(StringUtils.join(music.getTagList(),','));
        music.setListenNumb(0);
        save(music);
        Log log = new Log();
        log.setUserName("ID为 ".concat(id.toString().concat("的用户")));
        log.setDoSome("添加了歌曲");
        log.setMusicName(music.getMusicName());
        log.setCreateDate(LocalDate.now());
        logMapper.insert(log);
        return Mess.success().mess("添加成功");
    }

    @Override
    public Mess updateMusic(Music music, Integer id) {
        updateById(music);
        // 3. 远程调用upload模块删除文件
        Log log = new Log();
        log.setUserName("ID为 ".concat(id.toString().concat("的用户")));
        log.setDoSome("更新歌曲信息 歌曲名为：");
        log.setMusicName(music.getMusicName());
        log.setCreateDate(LocalDate.now());
        logMapper.insert(log);
        redisUtils.remove("musicID_".concat(String.valueOf(music.getMusicId())));
        return Mess.success().mess("修改成功");
    }

    @Override
    public Mess deleteMusic(Integer musicId,Integer id) {
        removeById(musicId);
        Log log = new Log();
        log.setDoSome("删除歌曲 歌曲ID为：");
        log.setUserName("ID为 ".concat(id.toString().concat("的用户")));
        log.setMusicName(id.toString());
        logMapper.insert(log);
        redisUtils.remove("musicID_".concat(String.valueOf(musicId)));
        return Mess.success().mess("删除成功");
    }

    @Override
    public Mess getMessage(Integer id) {
        QueryWrapper<Music> wrapper = new QueryWrapper<>();
        wrapper.eq("from_singer",id);
        long count = count(wrapper);
        wrapper.orderByDesc("listen_numb").last("limit 5");
        List<Music> musicList = list(wrapper);
        return Mess.success().data("musicList",musicList).data("count",count).data("numb",musicMapper.getMusicNumb(id));
    }
}