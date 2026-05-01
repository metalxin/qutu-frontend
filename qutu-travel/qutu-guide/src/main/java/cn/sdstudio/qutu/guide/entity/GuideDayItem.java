package cn.sdstudio.qutu.guide.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("t_guide_day_item")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "攻略日程条目")
public class GuideDayItem extends Model<GuideDayItem> {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "攻略ID")
    private Long guideId;

    @Schema(description = "第几天")
    private Integer dayIndex;

    @Schema(description = "类型：text|spot")
    private String type;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "关联景点ID")
    private Long spotId;

    @Schema(description = "排序")
    private Integer orderNum;
}
