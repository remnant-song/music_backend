package com.qst.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 歌曲收藏表
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Getter
@Setter
@ApiModel(value = "Mylike对象", description = "歌曲收藏表")
public class Mylike implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
      @TableId("user")
    private Integer user;

    @ApiModelProperty("歌曲id")
      @TableField("music")
    private Integer music;
}
