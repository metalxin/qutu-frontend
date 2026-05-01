package cn.sdstudio.qutu.story.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("t_story_image")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "故事图片")
public class StoryImage extends Model<StoryImage> {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long storyId;

    private String url;

    private Integer orderNum;
}
