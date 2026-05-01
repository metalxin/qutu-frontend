package cn.sdstudio.qutu.destination.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.log.annotation.SysLog;
import cn.sdstudio.qutu.common.security.annotation.HasPermission;
import cn.sdstudio.qutu.common.security.util.SecurityUtils;
import cn.sdstudio.qutu.destination.dto.SpotQueryDTO;
import cn.sdstudio.qutu.destination.entity.Spot;
import cn.sdstudio.qutu.destination.entity.SpotTip;
import cn.sdstudio.qutu.destination.service.SpotImageService;
import cn.sdstudio.qutu.destination.service.SpotService;
import cn.sdstudio.qutu.destination.service.SpotTipService;
import cn.sdstudio.qutu.destination.vo.SpotDetailVO;
import cn.sdstudio.qutu.destination.vo.SpotListVO;
import cn.sdstudio.qutu.destination.vo.TipVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 景点Controller
 *
 * @author qutu
 * @date 2026-02-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/spots")
@Tag(name = "景点管理", description = "景点相关接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SpotController {

    private final SpotService spotService;
    private final SpotImageService spotImageService;
    private final SpotTipService spotTipService;

    /**
     * 分页查询景点列表
     */
    @Operation(summary = "分页查询景点列表", description = "支持多条件筛选和排序")
    @GetMapping("/page")
    public R<?> getSpotPage(@ParameterObject Page<SpotListVO> page, 
                             @ParameterObject SpotQueryDTO query) {
        return R.ok(spotService.pageSpots(page, query));
    }

    /**
     * 获取景点详情
     */
    @Operation(summary = "获取景点详情", description = "获取景点详细信息，包括图片、提示、标签等")
    @GetMapping("/{id}")
    public R<SpotDetailVO> getSpotDetail(
            @Parameter(description = "景点ID") @PathVariable Long id) {
        Long userId = null;
        try {
            userId = SecurityUtils.getUser().getId();
        } catch (Exception ignored) {
        }
        return R.ok(spotService.getSpotDetail(id, userId));
    }

    /**
     * 获取城市下的景点
     */
    @Operation(summary = "获取城市下的景点", description = "根据城市ID获取景点列表")
    @GetMapping("/city/{cityId}")
    public R<List<SpotListVO>> getSpotsByCity(
            @Parameter(description = "城市ID") @PathVariable Long cityId,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "20") int limit) {
        return R.ok(spotService.getSpotsByCity(cityId, limit));
    }

    /**
     * 获取热门景点
     */
    @Operation(summary = "获取热门景点", description = "获取热门景点列表")
    @GetMapping("/hot")
    public R<List<SpotListVO>> getHotSpots(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        return R.ok(spotService.getHotSpots(limit));
    }

    /**
     * 获取附近景点
     */
    @Operation(summary = "获取附近景点", description = "根据经纬度获取附近景点")
    @GetMapping("/nearby")
    public R<List<SpotListVO>> getNearbySpots(
            @Parameter(description = "经度") @RequestParam BigDecimal lng,
            @Parameter(description = "纬度") @RequestParam BigDecimal lat,
            @Parameter(description = "搜索半径（公里）") @RequestParam(defaultValue = "10") int radius,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "20") int limit) {
        return R.ok(spotService.getNearbySpots(lng, lat, radius, limit));
    }

    /**
     * 获取景点关联攻略
     */
    @Operation(summary = "获取景点关联攻略", description = "根据景点ID获取关联攻略列表")
    @GetMapping("/{id}/guides")
    public R<?> getSpotGuides(
            @Parameter(description = "景点ID") @PathVariable Long id,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "3") int limit) {
        return R.ok(spotService.getRelatedGuides(id, limit));
    }

    /**
     * 更新景点关联攻略
     */
    @Operation(summary = "更新景点关联攻略", description = "设置景点关联的攻略ID列表")
    @SysLog("更新景点关联攻略")
    @PutMapping("/{id}/guides")
    @HasPermission("destination_spot_edit")
    public R<Boolean> updateSpotGuides(
            @Parameter(description = "景点ID") @PathVariable Long id,
            @RequestBody List<Long> guideIds) {
        return R.ok(spotService.updateSpotGuides(id, guideIds));
    }

    /**
     * 搜索景点
     */
    @Operation(summary = "搜索景点", description = "根据关键词搜索景点")
    @GetMapping("/search")
    public R<List<SpotListVO>> searchSpots(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "20") int limit) {
        return R.ok(spotService.searchSpots(keyword, limit));
    }

    /**
     * 收藏景点
     */
    @Operation(summary = "收藏景点", description = "将景点添加到收藏")
    @PostMapping("/{id}/favorite")
    public R<Boolean> favoriteSpot(
            @Parameter(description = "景点ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getUser().getId();
        return R.ok(spotService.favoriteSpot(id, userId));
    }

    /**
     * 取消收藏景点
     */
    @Operation(summary = "取消收藏景点", description = "从收藏中移除景点")
    @DeleteMapping("/{id}/favorite")
    public R<Boolean> unfavoriteSpot(
            @Parameter(description = "景点ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getUser().getId();
        return R.ok(spotService.unfavoriteSpot(id, userId));
    }

    /**
     * 取消收藏景点（POST方式）
     */
    @Operation(summary = "取消收藏景点", description = "从收藏中移除景点（兼容前端POST请求）")
    @PostMapping("/{id}/unfavorite")
    public R<Boolean> unfavoriteSpotPost(
            @Parameter(description = "景点ID") @PathVariable Long id) {
        Long userId = SecurityUtils.getUser().getId();
        return R.ok(spotService.unfavoriteSpot(id, userId));
    }

    /**
     * 获取用户收藏的景点
     */
    @Operation(summary = "获取用户收藏的景点", description = "分页获取当前用户收藏的景点")
    @GetMapping("/favorites")
    public R<?> getUserFavorites(@ParameterObject Page<SpotListVO> page) {
        Long userId = SecurityUtils.getUser().getId();
        return R.ok(spotService.getUserFavorites(page, userId));
    }

    // ========== 管理接口 ==========

    /**
     * 获取景点编辑详情（管理端，返回完整Spot实体）
     */
    @Operation(summary = "获取景点编辑详情", description = "管理端获取景点完整信息用于编辑回显")
    @GetMapping("/{id}/edit")
    @HasPermission("destination_spot_edit")
    public R<SpotDetailVO> getSpotForEdit(
            @Parameter(description = "景点ID") @PathVariable Long id) {
        return R.ok(spotService.getSpotDetail(id, null));
    }

    /**
     * 获取景点关联攻略ID列表（管理端）
     */
    @Operation(summary = "获取景点关联攻略ID列表", description = "管理端获取景点关联的攻略ID列表")
    @GetMapping("/{id}/guide-ids")
    @HasPermission("destination_spot_edit")
    public R<List<Long>> getSpotGuideIds(
            @Parameter(description = "景点ID") @PathVariable Long id) {
        return R.ok(spotService.getSpotGuideIds(id));
    }

    /**
     * 新增景点
     */
    @Operation(summary = "新增景点", description = "管理员新增景点")
    @SysLog("新增景点")
    @PostMapping
    @HasPermission("destination_spot_add")
    public R<Long> saveSpot(@RequestBody Spot spot) {
        boolean saved = spotService.save(spot);
        return R.ok(saved ? spot.getId() : null);
    }

    /**
     * 修改景点
     */
    @Operation(summary = "修改景点", description = "管理员修改景点")
    @SysLog("修改景点")
    @PutMapping
    @HasPermission("destination_spot_edit")
    public R<Boolean> updateSpot(@RequestBody Spot spot) {
        return R.ok(spotService.updateById(spot));
    }

    /**
     * 删除景点
     */
    @Operation(summary = "删除景点", description = "管理员删除景点")
    @SysLog("删除景点")
    @DeleteMapping("/{id}")
    @HasPermission("destination_spot_del")
    public R<Boolean> deleteSpot(
            @Parameter(description = "景点ID") @PathVariable Long id) {
        return R.ok(spotService.removeAdminSpot(id));
    }

    /**
     * 获取景点图集
     */
    @Operation(summary = "获取景点图集", description = "获取景点图片列表")
    @GetMapping("/{id}/images")
    public R<List<String>> getSpotImages(@PathVariable Long id) {
        return R.ok(spotImageService.getSpotImages(id));
    }

    /**
     * 更新景点图集
     */
    @Operation(summary = "更新景点图集", description = "先删除旧图集再保存新图集")
    @SysLog("更新景点图集")
    @PutMapping("/{id}/images")
    @HasPermission("destination_spot_edit")
    public R<Boolean> updateSpotImages(
            @Parameter(description = "景点ID") @PathVariable Long id,
            @RequestBody List<String> urls) {
        spotImageService.deleteBySpotId(id);
        spotImageService.saveImages(id, urls);
        return R.ok(true);
    }

    /**
     * 获取景点贴士
     */
    @Operation(summary = "获取景点贴士", description = "获取景点的旅行贴士列表")
    @GetMapping("/{id}/tips")
    public R<List<TipVO>> getSpotTips(@PathVariable Long id) {
        return R.ok(spotTipService.getSpotTips(id));
    }

    /**
     * 更新景点贴士
     */
    @Operation(summary = "更新景点贴士", description = "先删除旧贴士再保存新贴士")
    @SysLog("更新景点贴士")
    @PutMapping("/{id}/tips")
    @HasPermission("destination_spot_edit")
    public R<Boolean> updateSpotTips(
            @Parameter(description = "景点ID") @PathVariable Long id,
            @RequestBody List<TipVO> tips) {
        spotTipService.remove(Wrappers.<SpotTip>lambdaQuery().eq(SpotTip::getSpotId, id));
        if (tips != null && !tips.isEmpty()) {
            List<SpotTip> tipEntities = tips.stream().map(tip -> {
                SpotTip entity = new SpotTip();
                entity.setSpotId(id);
                entity.setIcon(tip.getIcon());
                entity.setTitle(tip.getTitle());
                entity.setDescription(tip.getDesc());
                entity.setOrderIndex(tip.getOrderIndex() != null ? tip.getOrderIndex() : 0);
                return entity;
            }).toList();
            spotTipService.saveBatch(tipEntities);
        }
        return R.ok(true);
    }
}
