package cn.sdstudio.qutu.destination.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.log.annotation.SysLog;
import cn.sdstudio.qutu.common.security.annotation.HasPermission;
import cn.sdstudio.qutu.destination.entity.SpotCategory;
import cn.sdstudio.qutu.destination.service.SpotCategoryService;
import cn.sdstudio.qutu.destination.vo.CategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 景点分类Controller
 *
 * @author qutu
 * @date 2026-02-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Tag(name = "分类管理", description = "景点分类相关接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class CategoryController {

    private final SpotCategoryService spotCategoryService;

    /**
     * 获取分类树
     */
    @Operation(summary = "获取分类树", description = "获取完整的分类树结构")
    @GetMapping("/tree")
    public R<List<CategoryVO>> getCategoryTree() {
        return R.ok(spotCategoryService.getCategoryTree());
    }

    /**
     * 获取顶级分类
     */
    @Operation(summary = "获取顶级分类", description = "获取所有顶级分类")
    @GetMapping("/roots")
    public R<List<CategoryVO>> getRootCategories() {
        return R.ok(spotCategoryService.getRootCategories());
    }

    /**
     * 获取子分类
     */
    @Operation(summary = "获取子分类", description = "获取指定分类下的子分类")
    @GetMapping("/{parentId}/children")
    public R<List<CategoryVO>> getSubCategories(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {
        return R.ok(spotCategoryService.getSubCategories(parentId));
    }

    // ========== 管理接口 ==========

    /**
     * 新增分类
     */
    @Operation(summary = "新增分类", description = "管理员新增景点分类")
    @SysLog("新增景点分类")
    @PostMapping
    @HasPermission("destination_category_add")
    public R<Boolean> saveCategory(@RequestBody SpotCategory category) {
        return R.ok(spotCategoryService.save(category));
    }

    /**
     * 修改分类
     */
    @Operation(summary = "修改分类", description = "管理员修改景点分类")
    @SysLog("修改景点分类")
    @PutMapping
    @HasPermission("destination_category_edit")
    public R<Boolean> updateCategory(@RequestBody SpotCategory category) {
        return R.ok(spotCategoryService.updateById(category));
    }

    /**
     * 删除分类
     */
    @Operation(summary = "删除分类", description = "管理员删除景点分类")
    @SysLog("删除景点分类")
    @DeleteMapping("/{id}")
    @HasPermission("destination_category_del")
    public R<Boolean> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        return R.ok(spotCategoryService.removeById(id));
    }
}
