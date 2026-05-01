package cn.sdstudio.qutu.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("t_guide_day")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "攻略日程")
public class GuideDay extends Model<GuideDay> {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "攻略ID")
    private Long guideId;

    @Schema(description = "第几天")
    private Integer dayIndex;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "摘要")
    private String summary;

    @Schema(description = "创建时间")
    @TableField(exist = false)
    private LocalDateTime createdAt;
}
