package cn.sdstudio.qutu.guide.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.guide.dto.GuideQueryDTO;
import cn.sdstudio.qutu.guide.service.GuideService;
import cn.sdstudio.qutu.guide.vo.GuideDetailVO;
import cn.sdstudio.qutu.guide.vo.GuideListVO;
import cn.sdstudio.qutu.guide.vo.GuideSummaryVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guides")
@Tag(name = "攻略", description = "攻略公开接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class GuideController {

    private final GuideService guideService;

    @Operation(summary = "精选攻略分页", description = "按关键词、城市、标签筛选与排序")
    @GetMapping("/page")
    public R<?> pageGuides(@Parameter(description = "分页") Page<GuideListVO> page,
                           @Parameter(description = "查询") GuideQueryDTO query) {
        return R.ok(guideService.pageGuides(page, query));
    }

    @Operation(summary = "攻略标签", description = "获取精选攻略标签（按使用次数）")
    @GetMapping("/tags")
    public R<List<cn.sdstudio.qutu.guide.vo.GuideTagVO>> tags(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "12") int limit) {
        return R.ok(guideService.getTags(limit));
    }

    @Operation(summary = "攻略详情", description = "获取攻略详情及日程")
    @GetMapping("/{id}")
    public R<GuideDetailVO> detail(@Parameter(description = "攻略ID") @PathVariable Long id) {
        return R.ok(guideService.getDetail(id));
    }

    @Operation(summary = "相关攻略", description = "根据景点ID获取相关攻略列表")
    @GetMapping("/related")
    public R<List<GuideSummaryVO>> relatedGuides(
            @Parameter(description = "景点ID") @RequestParam Long spotId,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "3") int limit) {
        return R.ok(guideService.relatedGuides(spotId, limit));
    }
}
