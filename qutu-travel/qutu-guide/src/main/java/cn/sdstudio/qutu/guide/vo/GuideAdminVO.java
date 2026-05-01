package cn.sdstudio.qutu.guide.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "攻略后台VO")
public class GuideAdminVO {

    @Schema(description = "攻略ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "封面图")
    private String coverUrl;

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

    @Schema(description = "关联城市ID")
    private Long cityId;

    @Schema(description = "关联城市名")
    private String cityName;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
