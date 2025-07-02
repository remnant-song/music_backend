package com.qst.domain.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Getter
@Setter
@ApiModel(value = "Log对象", description = "操作日志表")
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("创建时间")
    private LocalDate createDate;

    @ApiModelProperty("操作记录")
    private String doSome;

    @ApiModelProperty("操作歌曲记录")
    private String musicName;

    @ApiModelProperty("用户名")
    @TableField("userName")
    //给 userName 属性加上 @TableField("userName")，告诉 MyBatis-Plus 不要自动驼峰转下划线，直接用表里的列名：
    private String userName;
}
