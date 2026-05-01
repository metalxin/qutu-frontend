package cn.sdstudio.qutu.destination.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@TableName("t_spot_tag")
@EqualsAndHashCode(callSuper = true)
@Schema(description = "标签")
public class SpotTag extends Model<SpotTag> {

    /**
     * 标签ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "标签ID")
    private Long id;

    /**
     * 标签名
     */
    @Schema(description = "标签名")
    private String name;
}
