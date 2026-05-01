package cn.sdstudio.qutu.guide.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "攻略详情VO")
public class GuideDetailVO {

    @Schema(description = "攻略ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "封面图")
    private String cover;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "天数")
    private Integer days;

    @Schema(description = "晚数")
    private Integer nights;

    @Schema(description = "标签")
    private String tag;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "浏览量")
    private Long viewCount;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "日程列表")
    private List<GuideDayVO> itinerary;
}
