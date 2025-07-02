package com.qst.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户-歌单收藏表
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Getter
@Setter
@ApiModel(value = "Likelist对象", description = "用户-歌单收藏表")
public class Likelist implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("联合主键")
    @TableId // 标记联合主键
    private LikelistPK id;
//    @ApiModelProperty("歌单id")
//      @TableId("listId")
//    private Integer listId;
//
//    @ApiModelProperty("用户id")
//      @TableId("userId")
//    private Integer userId;
}
