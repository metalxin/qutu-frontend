package cn.sdstudio.qutu.destination.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "景点关联攻略VO")
public class SpotGuideVO {

    @Schema(description = "攻略ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "封面图")
    private String cover;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "行程时长文本")
    private String durationText;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "浏览量")
    private Long viewCount;

    @Schema(description = "标签")
    private String tag;
}
