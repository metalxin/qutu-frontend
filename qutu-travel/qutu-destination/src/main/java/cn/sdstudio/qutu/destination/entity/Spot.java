package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 景点
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_spot")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "景点")
public class Spot extends Model<Spot> {

    /**
     * 景点ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "景点ID")
    private Long id;

    /**
     * 城市ID
     */
    @Schema(description = "城市ID")
    private Long cityId;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Long categoryId;

    /**
     * 景点名称
     */
    @Schema(description = "景点名称")
    private String name;

    /**
     * 副标题
     */
    @Schema(description = "副标题")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private String subtitle;

    /**
     * 封面图
     */
    @Schema(description = "封面图")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private String coverUrl;

    /**
     * 评分
     */
    @Schema(description = "评分")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private BigDecimal rating;

    /**
     * 评分人数
     */
    @Schema(description = "评分人数")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Integer ratingCount;

    /**
     * 最低价
     */
    @Schema(description = "最低价")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private BigDecimal priceMin;

    /**
     * 最高价
     */
    @Schema(description = "最高价")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private BigDecimal priceMax;

    /**
     * 价格描述
     */
    @Schema(description = "价格描述")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private String priceText;

    /**
     * 游玩时长（分钟）
     */
    @Schema(description = "游玩时长（分钟）")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private Integer visitDurationMinutes;

    /**
     * 最佳季节
     */
    @Schema(description = "最佳季节")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private String bestSeason;

    /**
     * 适合人群
     */
    @Schema(description = "适合人群")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private String suitableFor;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private BigDecimal lat;

    /**
     * 经度
     */
    @Schema(description = "经度")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private BigDecimal lng;

    /**
     * 地址
     */
    @Schema(description = "地址")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private String address;

    /**
     * 详情
     */
    @Schema(description = "详情")
    @TableField(insertStrategy = FieldStrategy.ALWAYS)
    private String description;

    /**
     * 浏览量
     */
    @Schema(description = "浏览量")
    private Long viewCount;

    /**
     * 收藏数
     */
    @Schema(description = "收藏数")
    private Integer favoriteCount;

    /**
     * 评论数
     */
    @Schema(description = "评论数")
    private Integer commentCount;

    /**
     * 热度
     */
    @Schema(description = "热度")
    private BigDecimal hotScore;

    /**
     * 状态
     */
    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;

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
