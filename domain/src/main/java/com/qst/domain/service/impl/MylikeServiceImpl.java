package com.qst.domain.service.impl;

import com.qst.domain.entity.Mylike;
import com.qst.domain.mapper.MylikeMapper;
import com.qst.domain.service.IMylikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}
