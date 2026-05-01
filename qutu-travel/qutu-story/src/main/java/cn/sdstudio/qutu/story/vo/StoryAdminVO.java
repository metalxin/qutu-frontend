package cn.sdstudio.qutu.story.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 后台故事列表 VO
 */
@Data
@Schema(description = "后台故事列表VO")
public class StoryAdminVO {

    private Long id;

    private Long userId;

    private String username;

    private String content;

    private String firstImage;

    private Integer imagesCount;

    private String locationName;

    private LocalDate recordDate;

    private String mood;

    private String weather;

    private Integer visibility;

    private LocalDateTime createdAt;
}
