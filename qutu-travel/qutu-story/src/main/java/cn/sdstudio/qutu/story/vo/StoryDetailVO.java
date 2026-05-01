package cn.sdstudio.qutu.story.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "故事详情VO")
public class StoryDetailVO {
    private Long id;
    private String content;
    private List<String> images;
    private String locationName;
    private java.math.BigDecimal lng;
    private java.math.BigDecimal lat;
    private LocalDate recordDate;
    private String weekday;
    private String mood;
    private String weather;
}
