package com.qst.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户消息表
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Getter
@Setter
@ApiModel(value = "Msg对象", description = "用户消息表")
public class Msg implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("消息id")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("消息标题")
    private String title;

    @ApiModelProperty("消息内容")
    private String msg;

    @ApiModelProperty("是否是已读消息（0未读，1已读）")
    private Integer isread;

    @ApiModelProperty("创建时间")
    private LocalDate createTime;
}
