package cn.sdstudio.qutu.destination.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 景点详情VO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "景点详情VO")
public class SpotDetailVO {

    @Schema(description = "景点ID")
    private Long id;

    @Schema(description = "景点名称")
    private String name;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "封面图")
    private String cover;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "评论数量")
    private Integer reviewCount;

    @Schema(description = "评分来源")
    private String reviewSource;

    @Schema(description = "距离")
    private String distance;

    @Schema(description = "详情描述")
    private String description;

    @Schema(description = "位置标题")
    private String locationTitle;

    @Schema(description = "位置详情")
    private String locationDetail;

    @Schema(description = "价格")
    private String price;

    @Schema(description = "价格备注")
    private String priceNote;

    @Schema(description = "图片列表")
    private List<String> gallery;

    @Schema(description = "提示信息")
    private List<TipVO> tips;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "是否已收藏")
    private Boolean isFavorite;

    @Schema(description = "收藏数")
    private Integer favoriteCount;

    @Schema(description = "浏览量")
    private Long viewCount;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "最佳季节")
    private String bestSeason;

    @Schema(description = "适合人群")
    private String suitableFor;

    @Schema(description = "游玩时长（分钟）")
    private Integer visitDurationMinutes;

    @Schema(description = "经度")
    private BigDecimal lng;

    @Schema(description = "纬度")
    private BigDecimal lat;

    @Schema(description = "城市ID")
    private Long cityId;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "封面图URL（管理端编辑使用）")
    private String coverUrl;

    @Schema(description = "地址（管理端编辑使用）")
    private String address;

    @Schema(description = "评分人数（管理端编辑使用）")
    private Integer ratingCount;

    @Schema(description = "最低价（管理端编辑使用）")
    private BigDecimal priceMin;

    @Schema(description = "最高价（管理端编辑使用）")
    private BigDecimal priceMax;

    @Schema(description = "价格描述（管理端编辑使用）")
    private String priceText;

    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;

    @Schema(description = "关联攻略列表")
    private List<SpotGuideVO> relatedGuides;
}
