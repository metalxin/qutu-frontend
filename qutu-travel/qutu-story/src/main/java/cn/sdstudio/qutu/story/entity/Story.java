package cn.sdstudio.qutu.story.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_story")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "旅行故事")
public class Story extends Model<Story> {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long userId;

    private String content;

    private String locationName;

    private java.math.BigDecimal lng;

    private java.math.BigDecimal lat;

    private LocalDate recordDate;

    private String mood;

    private String weather;

    private Integer imagesCount;

    private Integer visibility;

    private Long spotId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deletedFlag;
}
