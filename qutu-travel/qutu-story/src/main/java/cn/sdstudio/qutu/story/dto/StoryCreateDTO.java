package cn.sdstudio.qutu.story.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "旅行故事创建DTO")
public class StoryCreateDTO {

    private String content;

    private String locationName;

    private BigDecimal lng;

    private BigDecimal lat;

    private LocalDate recordDate;

    private String mood;

    private String weather;

    private Long spotId;

    private List<String> images;
}
