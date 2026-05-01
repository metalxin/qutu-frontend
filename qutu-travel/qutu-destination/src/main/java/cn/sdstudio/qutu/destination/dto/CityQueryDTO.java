package cn.sdstudio.qutu.destination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 目的地城市查询DTO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "目的地城市查询DTO")
public class CityQueryDTO {

    @Schema(description = "城市名称")
    private String name;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "区域名称")
    private String region;

    @Schema(description = "是否热门")
    private Integer isHot;

    @Schema(description = "最低评分")
    private BigDecimal minRating;

    @Schema(description = "关键词搜索")
    private String keyword;
}
