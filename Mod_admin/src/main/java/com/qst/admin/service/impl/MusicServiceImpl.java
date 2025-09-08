package com.qst.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qst.domain.entity.Mess;
import com.qst.domain.entity.Music;
import com.qst.admin.mapper.MusicMapper;
import com.qst.admin.service.IMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌曲信息表 服务实现类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements IMusicService {
    @Autowired
    MusicMapper musicMapper;

    @Override
    public Mess searchMusic(Integer pn, Integer size, String keyword) {
        Page<Music> page = new Page<>(pn,size);
        IPage<Music> musicIPage = musicMapper.SearchMusic(page, keyword);
        return Mess.success().data("page",musicIPage);
    }

    @Override
    public Mess freezeMusic(Integer musicId) {
        UpdateWrapper<Music> wrapper = new UpdateWrapper<>();
        wrapper.eq("music_id",musicId).set("activation",2);
        update(wrapper);
        return Mess.success().mess("冻结成功");
    }

    @Override
    public Mess unFreezeMusic(Integer musicId) {
        UpdateWrapper<Music> wrapper = new UpdateWrapper<>();
        wrapper.eq("music_id",musicId).set("activation",0);
        update(wrapper);
        return Mess.success().mess("解冻成功");
    }
}
