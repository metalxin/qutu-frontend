package cn.sdstudio.qutu.destination.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 景点列表VO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "景点列表VO")
public class SpotListVO {

    @Schema(description = "景点ID")
    private Long id;

    @Schema(description = "景点名称")
    private String name;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "封面图")
    private String coverUrl;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "评分人数")
    private Integer ratingCount;

    @Schema(description = "价格描述")
    private String priceText;

    @Schema(description = "距离（km）")
    private String distance;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "标签列表")
    private List<String> tags;

    @Schema(description = "收藏数")
    private Integer favoriteCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "浏览量")
    private Long viewCount;

    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;
}
