package com.qst.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 歌单信息表
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Getter
@Setter
@TableName("song_list")
@ApiModel(value = "SongList对象", description = "歌单信息表")
@Data
@EqualsAndHashCode(callSuper = false)
public class SongList implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer user;

    private String image;

    private String message;

    private LocalDate createDate;

    private String tags;

    //携带的用户名
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private Integer isLike=0;

}