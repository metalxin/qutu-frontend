package cn.sdstudio.qutu.guide.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "攻略查询DTO")
public class GuideQueryDTO {

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "城市ID")
    private Long cityId;

    @Schema(description = "城市名")
    private String cityName;

    @Schema(description = "标签")
    private String tag;

    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;

    @Schema(description = "最小天数")
    private Integer minDays;

    @Schema(description = "最大天数")
    private Integer maxDays;

    @Schema(description = "排序字段：viewCount|likeCount|createdAt")
    private String orderBy;

    @Schema(description = "排序方向：asc|desc")
    private String orderDirection;
}
