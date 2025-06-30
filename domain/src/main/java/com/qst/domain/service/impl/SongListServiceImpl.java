package com.qst.domain.service.impl;

import com.qst.domain.entity.SongList;
import com.qst.domain.mapper.SongListMapper;
import com.qst.domain.service.ISongListService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 歌单信息表 服务实现类
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Service
public class SongListServiceImpl extends ServiceImpl<SongListMapper, SongList> implements ISongListService {

}
