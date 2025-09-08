package com.qst.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 歌曲信息表
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Getter
@Setter
@TableName("music")
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Music对象", description="")
public class Music implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "music_id", type = IdType.AUTO)
    private Integer musicId;

    @ApiModelProperty(value = "对应的用户")
    private Integer fromSinger;

    @ApiModelProperty(value = "歌曲名")
    private String musicName;

    @ApiModelProperty(value = "地址（对应UUID名）")
    private String musicUrl;

    @ApiModelProperty(value = "是否激活（0激活 1用户锁定 2管理员锁定）")
    private Integer activation;

    @ApiModelProperty(value = "播放量")
    private Integer listenNumb;

    private String imageUrl;

    private Integer timelength;

    private LocalDate createTime;

    private String tags;

    private String lyric;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private List<String> tagList;

    @TableField(exist = false)
    private Integer isLike=0;

}