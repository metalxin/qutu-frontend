package cn.sdstudio.qutu.story.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 后台故事编辑 DTO
 */
@Data
@Schema(description = "后台故事编辑DTO")
public class StoryAdminDTO {

    private Long id;

    private Long userId;

    private String content;

    private String locationName;

    private BigDecimal lng;

    private BigDecimal lat;

    private LocalDate recordDate;

    private String mood;

    private String weather;

    private Integer visibility;

    private Long spotId;

    private List<String> images;
}
