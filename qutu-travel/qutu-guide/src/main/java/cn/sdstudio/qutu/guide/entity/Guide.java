package cn.sdstudio.qutu.guide.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("t_guide")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "攻略")
public class Guide extends Model<Guide> {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
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

    @Schema(description = "状态：0-下架，1-上架")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deletedFlag;
}
