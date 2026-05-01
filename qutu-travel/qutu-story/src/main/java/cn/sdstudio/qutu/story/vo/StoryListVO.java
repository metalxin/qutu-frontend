package cn.sdstudio.qutu.story.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "故事列表VO")
public class StoryListVO {

    private Long id;
    private String content;
    private String firstImage;
    private Integer imagesCount;
    private String locationName;
    private LocalDate recordDate;
    private String weekday;
    private String mood;
    private String weather;
}
