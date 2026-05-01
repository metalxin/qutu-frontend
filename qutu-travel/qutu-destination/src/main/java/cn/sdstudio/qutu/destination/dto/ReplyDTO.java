package cn.sdstudio.qutu.destination.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 回复提交DTO
 *
 * @author qutu
 * @date 2026-02-02
 */
@Data
@Schema(description = "回复提交DTO")
public class ReplyDTO {

    @Schema(description = "父评论ID")
    @NotNull(message = "父评论ID不能为空")
    private Long parentId;

    @Schema(description = "回复内容")
    @NotBlank(message = "回复内容不能为空")
    private String content;

    @Schema(description = "被回复者用户名")
    private String replyTo;
}
