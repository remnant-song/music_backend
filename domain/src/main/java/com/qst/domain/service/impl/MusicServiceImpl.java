package com.qst.domain.service.impl;

import com.qst.domain.entity.Music;
import com.qst.domain.mapper.MusicMapper;
import com.qst.domain.service.IMusicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
