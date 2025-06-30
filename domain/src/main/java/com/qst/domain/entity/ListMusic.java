package com.qst.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 歌单-歌曲关系表
 * </p>
 *
 * @author student
 * @since 2025-06-30
 */
@Getter
@Setter
@TableName("list_music")
@ApiModel(value = "ListMusic对象", description = "歌单-歌曲关系表")
public class ListMusic implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("歌单id")
      @TableId("listid")
    private Integer listid;

    @ApiModelProperty("歌曲id")
      @TableId("music")
    private Integer music;
}
