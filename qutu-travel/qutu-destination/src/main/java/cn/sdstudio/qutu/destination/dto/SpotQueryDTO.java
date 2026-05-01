package cn.sdstudio.qutu.destination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 景点查询DTO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "景点查询DTO")
public class SpotQueryDTO {

    @Schema(description = "城市ID")
    private Long cityId;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "景点名称")
    private String name;

    @Schema(description = "关键词搜索")
    private String keyword;

    @Schema(description = "最低评分")
    private BigDecimal minRating;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "标签ID")
    private Long tagId;

    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "排序字段：rating-评分，hotScore-热度，viewCount-浏览量")
    private String orderBy;

    @Schema(description = "排序方向：asc-升序，desc-降序")
    private String orderDirection;

    @Schema(description = "用户经度，用于计算距离")
    private BigDecimal userLng;

    @Schema(description = "用户纬度，用于计算距离")
    private BigDecimal userLat;

    @Schema(description = "搜索半径（公里）")
    private Integer radius;
}
