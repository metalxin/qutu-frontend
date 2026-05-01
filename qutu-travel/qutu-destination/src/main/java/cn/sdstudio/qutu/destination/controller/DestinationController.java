package cn.sdstudio.qutu.destination.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.log.annotation.SysLog;
import cn.sdstudio.qutu.common.security.annotation.HasPermission;
import cn.sdstudio.qutu.common.security.annotation.Inner;
import cn.sdstudio.qutu.destination.dto.CityQueryDTO;
import cn.sdstudio.qutu.destination.entity.DestinationCity;
import cn.sdstudio.qutu.destination.service.DestinationCityService;
import cn.sdstudio.qutu.destination.vo.DestinationVO;
import cn.sdstudio.qutu.destination.vo.RegionGroupVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 目的地城市Controller
 *
 * @author qutu
 * @date 2026-02-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/destinations")
@Tag(name = "目的地管理", description = "目的地城市相关接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class DestinationController {

    private final DestinationCityService destinationCityService;

    /**
     * 分页查询目的地列表
     */
    @Operation(summary = "分页查询目的地列表", description = "支持按地区、省份、关键词等条件筛选")
    @GetMapping("/page")
    public R<?> getDestinationPage(@ParameterObject Page<DestinationVO> page, 
                                    @ParameterObject CityQueryDTO query) {
        return R.ok(destinationCityService.pageDestinations(page, query));
    }

    /**
     * 获取目的地列表（不分页）
     */
    @Operation(summary = "获取目的地列表", description = "获取目的地列表，可按区域筛选")
    @GetMapping
    public R<List<DestinationVO>> getDestinations(@ParameterObject CityQueryDTO query) {
        Page<DestinationVO> page = new Page<>(1, 100);
        return R.ok(destinationCityService.pageDestinations(page, query).getRecords());
    }

    /**
     * 获取目的地详情
     */
    @Operation(summary = "获取目的地详情", description = "根据ID获取目的地详细信息")
    @GetMapping("/{id}")
    public R<DestinationVO> getDestinationDetail(
            @Parameter(description = "目的地ID") @PathVariable Long id) {
        destinationCityService.incrementViewCount(id);
        return R.ok(destinationCityService.getDestinationDetail(id));
    }

    /**
     * 获取热门城市
     */
    @Operation(summary = "获取热门城市", description = "获取热门城市名称列表")
    @GetMapping("/cities/hot")
    public R<List<String>> getHotCities(
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "18") int limit) {
        return R.ok(destinationCityService.getHotCities(limit));
    }

    /**
     * 获取地区分组
     */
    @Operation(summary = "获取地区分组", description = "获取按地区分组的省份列表")
    @GetMapping("/regions")
    public R<List<RegionGroupVO>> getRegionGroups() {
        return R.ok(destinationCityService.getRegionGroups());
    }

    /**
     * 根据省份获取城市
     */
    @Operation(summary = "根据省份获取城市", description = "获取指定省份下的城市列表")
    @GetMapping("/cities")
    public R<List<DestinationVO>> getCitiesByProvince(
            @Parameter(description = "省份名称") @RequestParam String province) {
        return R.ok(destinationCityService.getCitiesByProvince(province));
    }

    /**
     * 搜索目的地
     */
    @Operation(summary = "搜索目的地", description = "根据关键词搜索目的地")
    @GetMapping("/search")
    public R<List<DestinationVO>> searchDestinations(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        return R.ok(destinationCityService.searchDestinations(keyword, limit));
    }

    // ========== 管理接口 ==========

    /**
     * 新增目的地
     */
    @Operation(summary = "新增目的地", description = "管理员新增目的地城市")
    @SysLog("新增目的地")
    @PostMapping
    @HasPermission("destination_city_add")
    public R<Boolean> saveDestination(@RequestBody DestinationCity city) {
        return R.ok(destinationCityService.save(city));
    }

    /**
     * 修改目的地
     */
    @Operation(summary = "修改目的地", description = "管理员修改目的地城市")
    @SysLog("修改目的地")
    @PutMapping
    @HasPermission("destination_city_edit")
    public R<Boolean> updateDestination(@RequestBody DestinationCity city) {
        return R.ok(destinationCityService.updateById(city));
    }

    /**
     * 删除目的地
     */
    @Operation(summary = "删除目的地", description = "管理员删除目的地城市")
    @SysLog("删除目的地")
    @DeleteMapping("/{id}")
    @HasPermission("destination_city_del")
    public R<Boolean> deleteDestination(
            @Parameter(description = "目的地ID") @PathVariable Long id) {
        return R.ok(destinationCityService.removeById(id));
    }

    /**
     * 刷新所有城市的景点数量
     */
    @Operation(summary = "刷新景点数量", description = "重新统计所有城市的景点数量")
    @SysLog("刷新景点数量")
    @PostMapping("/refresh-spots-count")
    @HasPermission("destination_city_edit")
    public R<Boolean> refreshSpotsCount() {
        destinationCityService.refreshAllSpotsCount();
        return R.ok(true);
    }
}
