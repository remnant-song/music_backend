package com.qst.domain.service.impl;

import com.qst.domain.entity.ListMusic;
import com.qst.domain.mapper.ListMusicMapper;
import com.qst.domain.service.IListMusicService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
