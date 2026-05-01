package cn.sdstudio.qutu.story.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 后台故事查询 DTO
 */
@Data
@Schema(description = "故事查询DTO")
public class StoryQueryDTO {

    @Schema(description = "关键词搜索（内容/地点）")
    private String keyword;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "记录日期")
    private LocalDate recordDate;

    @Schema(description = "心情")
    private String mood;

    @Schema(description = "天气")
    private String weather;

    @Schema(description = "可见性：1仅自己 2好友 3公开")
    private Integer visibility;
}
