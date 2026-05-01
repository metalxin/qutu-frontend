package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签关联
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_spot_tag_map")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "标签关联")
public class SpotTagMap extends Model<SpotTagMap> {

    /**
     * 景点ID
     */
    @Schema(description = "景点ID")
    private Long spotId;

    /**
     * 标签ID
     */
    @Schema(description = "标签ID")
    private Long tagId;
}
