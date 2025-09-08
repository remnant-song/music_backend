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
 * 用户表
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Getter
@Setter
@ApiModel(value = "User对象", description = "用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("邮箱（不重复）")
    private String email;

    @ApiModelProperty("手机号（不重复）")
    private String phone;

    @ApiModelProperty("规则（0为管理员 1为歌手 2为普通用户）")
    private Integer role;

    @ApiModelProperty("用户简介")
    private String about;

    @ApiModelProperty("是否激活（针对歌手）")
    private Integer activation;

    @ApiModelProperty("头像url")
    private String imageUrl;

    @ApiModelProperty("创建时间")
    private LocalDate createTime;
}
