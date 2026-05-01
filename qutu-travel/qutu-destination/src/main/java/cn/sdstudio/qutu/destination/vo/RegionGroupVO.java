package cn.sdstudio.qutu.destination.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 地区分组VO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "地区分组VO")
public class RegionGroupVO {

    @Schema(description = "地区名称")
    private String name;

    @Schema(description = "省份列表")
    private List<String> provinces;
}
