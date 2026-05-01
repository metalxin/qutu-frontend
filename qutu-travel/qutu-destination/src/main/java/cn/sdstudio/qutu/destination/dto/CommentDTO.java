package cn.sdstudio.qutu.destination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 评论提交DTO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "评论提交DTO")
public class CommentDTO {

    @Schema(description = "景点ID")
    @NotNull(message = "景点ID不能为空")
    private Long spotId;

    @Schema(description = "评论内容")
    @NotBlank(message = "评论内容不能为空")
    private String content;

    @Schema(description = "图片URL列表")
    private List<String> images;
}
