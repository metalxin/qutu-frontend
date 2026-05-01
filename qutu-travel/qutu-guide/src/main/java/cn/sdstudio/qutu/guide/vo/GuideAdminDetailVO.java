package cn.sdstudio.qutu.guide.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "攻略后台详情VO")
public class GuideAdminDetailVO extends GuideAdminVO {

    @Schema(description = "行程列表")
    private List<GuideDayAdminVO> itinerary = new ArrayList<>();
}
