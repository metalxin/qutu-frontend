package cn.sdstudio.qutu.destination.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 目的地城市VO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "目的地城市VO")
public class DestinationVO {

    @Schema(description = "城市ID")
    private Long id;

    @Schema(description = "城市名称")
    private String name;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "封面图")
    private String image;

    @Schema(description = "渐变色")
    private String gradient;

    @Schema(description = "景点数量")
    private Integer spots;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "标签")
    private String tag;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "简介")
    private String intro;
}
