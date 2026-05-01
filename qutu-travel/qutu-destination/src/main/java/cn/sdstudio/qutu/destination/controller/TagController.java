package cn.sdstudio.qutu.destination.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.log.annotation.SysLog;
import cn.sdstudio.qutu.common.security.annotation.HasPermission;
import cn.sdstudio.qutu.destination.entity.SpotTag;
import cn.sdstudio.qutu.destination.service.SpotTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签Controller
 *
 * @author qutu
 * @date 2026-02-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/tags")
@Tag(name = "标签管理", description = "景点标签相关接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class TagController {

    private final SpotTagService spotTagService;

    /**
     * 获取所有标签
     */
    @Operation(summary = "获取所有标签", description = "获取所有可用标签")
    @GetMapping
    public R<List<SpotTag>> getAllTags() {
        return R.ok(spotTagService.list());
    }

    /**
     * 获取热门标签
     */
    @Operation(summary = "获取热门标签", description = "获取热门标签列表")
    @GetMapping("/hot")
    public R<List<SpotTag>> getHotTags(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "20") int limit) {
        return R.ok(spotTagService.getHotTags(limit));
    }

    /**
     * 获取景点标签
     */
    @Operation(summary = "获取景点标签", description = "获取指定景点的标签")
    @GetMapping("/spot/{spotId}")
    public R<List<String>> getSpotTags(
            @Parameter(description = "景点ID") @PathVariable Long spotId) {
        return R.ok(spotTagService.getSpotTags(spotId));
    }

    // ========== 管理接口 ==========

    /**
     * 新增标签
     */
    @Operation(summary = "新增标签", description = "管理员新增标签")
    @SysLog("新增标签")
    @PostMapping
    @HasPermission("destination_tag_add")
    public R<Boolean> saveTag(@RequestBody SpotTag tag) {
        return R.ok(spotTagService.save(tag));
    }

    /**
     * 修改标签
     */
    @Operation(summary = "修改标签", description = "管理员修改标签")
    @SysLog("修改标签")
    @PutMapping
    @HasPermission("destination_tag_edit")
    public R<Boolean> updateTag(@RequestBody SpotTag tag) {
        return R.ok(spotTagService.updateById(tag));
    }

    /**
     * 删除标签
     */
    @Operation(summary = "删除标签", description = "管理员删除标签")
    @SysLog("删除标签")
    @DeleteMapping("/{id}")
    @HasPermission("destination_tag_del")
    public R<Boolean> deleteTag(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        return R.ok(spotTagService.removeById(id));
    }

    /**
     * 设置景点标签
     */
    @Operation(summary = "设置景点标签", description = "管理员设置景点的标签")
    @SysLog("设置景点标签")
    @PutMapping("/spot/{spotId}")
    @HasPermission("destination_spot_edit")
    public R<Boolean> setSpotTags(
            @Parameter(description = "景点ID") @PathVariable Long spotId,
            @RequestBody List<Long> tagIds) {
        return R.ok(spotTagService.setSpotTags(spotId, tagIds));
    }
}
