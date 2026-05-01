package cn.sdstudio.qutu.guide.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "攻略日程DTO")
public class GuideDayDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "第几天")
    private Integer dayIndex;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "条目列表")
    private List<GuideDayItemDTO> items = new ArrayList<>();
}
