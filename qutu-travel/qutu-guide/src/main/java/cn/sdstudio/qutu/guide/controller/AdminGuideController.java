package cn.sdstudio.qutu.guide.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.log.annotation.SysLog;
import cn.sdstudio.qutu.common.security.annotation.HasPermission;
import cn.sdstudio.qutu.guide.dto.GuideAdminDTO;
import cn.sdstudio.qutu.guide.dto.GuideQueryDTO;
import cn.sdstudio.qutu.guide.service.GuideService;
import cn.sdstudio.qutu.guide.vo.GuideAdminDetailVO;
import cn.sdstudio.qutu.guide.vo.GuideAdminVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/guide-admin")
@Tag(name = "攻略后台", description = "攻略后台管理接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class AdminGuideController {

    private final GuideService guideService;

    @Operation(summary = "攻略后台分页", description = "攻略后台分页查询")
    @GetMapping("/page")
    public R<?> pageGuides(@ParameterObject Page<GuideAdminVO> page,
                           @ParameterObject GuideQueryDTO query) {
        return R.ok(guideService.pageAdminGuides(page, query));
    }

    @Operation(summary = "攻略后台详情", description = "获取攻略后台编辑详情")
    @GetMapping("/{id}")
    public R<GuideAdminDetailVO> getGuide(@Parameter(description = "攻略ID") @PathVariable Long id) {
        return R.ok(guideService.getAdminDetail(id));
    }

    @Operation(summary = "新增攻略", description = "新增攻略及行程")
    @SysLog("新增攻略")
    @PostMapping
    @HasPermission("guide_add")
    public R<Boolean> saveGuide(@RequestBody GuideAdminDTO dto) {
        return R.ok(guideService.saveAdminGuide(dto));
    }

    @Operation(summary = "修改攻略", description = "修改攻略及行程")
    @SysLog("修改攻略")
    @PutMapping
    @HasPermission("guide_edit")
    public R<Boolean> updateGuide(@RequestBody GuideAdminDTO dto) {
        return R.ok(guideService.updateAdminGuide(dto));
    }

    @Operation(summary = "删除攻略", description = "删除攻略及行程")
    @SysLog("删除攻略")
    @DeleteMapping("/{id}")
    @HasPermission("guide_del")
    public R<Boolean> deleteGuide(@Parameter(description = "攻略ID") @PathVariable Long id) {
        return R.ok(guideService.removeAdminGuide(id));
    }
}
