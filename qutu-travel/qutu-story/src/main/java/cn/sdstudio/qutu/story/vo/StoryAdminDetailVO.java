package cn.sdstudio.qutu.story.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台故事详情 VO
 */
@Data
@Schema(description = "后台故事详情VO")
public class StoryAdminDetailVO {

    private Long id;

    private Long userId;

    private String username;

    private String content;

    private List<String> images;

    private String locationName;

    private BigDecimal lng;

    private BigDecimal lat;

    private LocalDate recordDate;

    private String mood;

    private String weather;

    private Integer visibility;

    private Long spotId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
