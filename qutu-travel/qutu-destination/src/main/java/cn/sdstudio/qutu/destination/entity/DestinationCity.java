package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 目的地城市
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_destination_city")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "目的地城市")
public class DestinationCity extends Model<DestinationCity> {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 城市名称
     */
    @Schema(description = "城市名称")
    private String name;

    /**
     * 省份
     */
    @Schema(description = "省份")
    private String province;

    /**
     * 国家
     */
    @Schema(description = "国家")
    private String country;

    /**
     * 副标题
     */
    @Schema(description = "副标题")
    private String subtitle;

    /**
     * 简介
     */
    @Schema(description = "简介")
    private String intro;

    /**
     * 封面图
     */
    @Schema(description = "封面图")
    private String coverUrl;

    /**
     * 横幅图
     */
    @Schema(description = "横幅图")
    private String bannerUrl;

    /**
     * 中心纬度
     */
    @Schema(description = "中心纬度")
    private BigDecimal centerLat;

    /**
     * 中心经度
     */
    @Schema(description = "中心经度")
    private BigDecimal centerLng;

    /**
     * 景点数量
     */
    @Schema(description = "景点数量")
    private Integer spotsCount;

    /**
     * 平均评分
     */
    @Schema(description = "平均评分")
    private BigDecimal avgRating;

    /**
     * 浏览量
     */
    @Schema(description = "浏览量")
    private Long viewCount;

    /**
     * 热度值
     */
    @Schema(description = "热度值")
    private BigDecimal hotScore;

    /**
     * 是否热门
     */
    @Schema(description = "是否热门：0-否，1-是")
    private Integer isHot;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除
     */
    @Schema(description = "逻辑删除：0-未删除，1-已删除")
    @TableLogic
    private Integer deletedFlag;
}
