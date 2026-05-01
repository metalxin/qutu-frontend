package cn.sdstudio.qutu.guide.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "攻略景点关联")
public class GuideSpotMap {

    @Schema(description = "攻略ID")
    private Long guideId;

    @Schema(description = "景点ID")
    private Long spotId;
}
