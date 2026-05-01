package cn.sdstudio.qutu.checklist.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.security.util.SecurityUtils;
import cn.sdstudio.qutu.checklist.dto.BudgetDTO;
import cn.sdstudio.qutu.checklist.dto.ChecklistCreateDTO;
import cn.sdstudio.qutu.checklist.dto.ChecklistItemDTO;
import cn.sdstudio.qutu.checklist.dto.ChecklistItemToggleDTO;
import cn.sdstudio.qutu.checklist.dto.ExpenseDTO;
import cn.sdstudio.qutu.checklist.service.ChecklistService;
import cn.sdstudio.qutu.checklist.vo.ChecklistDetailVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistListVO;
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
import java.util.Objects;

/**
 * 行前清单用户端接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/checklists")
@Tag(name = "行前清单", description = "行前清单用户端接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class ChecklistController {

    private final ChecklistService checklistService;

    @Operation(summary = "我的清单列表", description = "分页获取当前用户的清单")
    @GetMapping("/page")
    public R<?> page(@ParameterObject Page<ChecklistListVO> page) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            Page<ChecklistListVO> empty = new Page<>(page.getCurrent(), page.getSize());
            return R.ok(empty);
        }
        return R.ok(checklistService.pageMyChecklists(page, userId));
    }

    @Operation(summary = "清单详情", description = "获取清单详情（含清单项）")
    @GetMapping("/{id}")
    public R<ChecklistDetailVO> detail(@Parameter(description = "清单ID") @PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        ChecklistDetailVO detail = checklistService.getMyDetail(id, userId);
        if (detail == null) {
            return R.failed("清单不存在或无权限(checklistId=" + id + ", userId=" + userId + ")");
        }
        return R.ok(detail);
    }

    @Operation(summary = "创建清单", description = "用户创建新清单")
    @PostMapping
    public R<Long> create(@RequestBody ChecklistCreateDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        return R.ok(checklistService.createMyChecklist(userId, dto));
    }

    @Operation(summary = "更新清单", description = "用户更新清单信息及清单项")
    @PutMapping("/{id}")
    public R<Boolean> update(@Parameter(description = "清单ID") @PathVariable Long id,
                              @RequestBody ChecklistCreateDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        boolean success = checklistService.updateMyChecklist(id, userId, dto);
        return success ? R.ok(true) : R.failed("更新失败：清单不存在或无权限");
    }

    @Operation(summary = "删除清单", description = "用户删除自己的清单")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@Parameter(description = "清单ID") @PathVariable Long id) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        boolean success = checklistService.removeMyChecklist(id, userId);
        return success ? R.ok(true) : R.failed("删除失败：清单不存在或无权限(checklistId=" + id + ", userId=" + userId + ")");
    }

    @Operation(summary = "勾选/取消勾选清单项", description = "切换清单项的勾选状态")
    @PutMapping("/items/{itemId}/toggle")
    public R<Boolean> toggleItem(@Parameter(description = "清单项ID") @PathVariable Long itemId,
                                  @RequestBody ChecklistItemToggleDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        boolean success = checklistService.toggleItem(itemId, userId, dto.getChecked());
        return success ? R.ok(true) : R.failed("勾选失败：清单项不存在或无权限(itemId=" + itemId + ", userId=" + userId + ")");
    }

    @Operation(summary = "添加清单项", description = "向清单中添加新项")
    @PostMapping("/{checklistId}/items")
    public R<Long> addItem(@Parameter(description = "清单ID") @PathVariable Long checklistId,
                            @RequestBody ChecklistItemDTO itemDTO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        Long itemId = checklistService.addItem(checklistId, userId, itemDTO);
        return itemId != null ? R.ok(itemId) : R.failed("添加失败：清单不存在或无权限(checklistId=" + checklistId + ", userId=" + userId + ")");
    }

    @Operation(summary = "删除清单项", description = "删除清单中的某一项")
    @DeleteMapping("/items/{itemId}")
    public R<Boolean> removeItem(@Parameter(description = "清单项ID") @PathVariable Long itemId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        boolean success = checklistService.removeItem(itemId, userId);
        return success ? R.ok(true) : R.failed("删除失败：清单项不存在或无权限(itemId=" + itemId + ", userId=" + userId + ")");
    }

    // ========== 费用接口 ==========

    @Operation(summary = "获取费用列表", description = "获取清单下的所有费用记录")
    @GetMapping("/{checklistId}/expenses")
    public R<?> listExpenses(@Parameter(description = "清单ID") @PathVariable Long checklistId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        return R.ok(checklistService.listExpenses(checklistId, userId));
    }

    @Operation(summary = "添加费用", description = "向清单添加费用记录")
    @PostMapping("/{checklistId}/expenses")
    public R<Long> addExpense(@Parameter(description = "清单ID") @PathVariable Long checklistId,
                               @RequestBody ExpenseDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        Long expenseId = checklistService.addExpense(checklistId, userId, dto);
        return expenseId != null ? R.ok(expenseId) : R.failed("记账失败：清单不存在或无权限(checklistId=" + checklistId + ", userId=" + userId + ")");
    }

    @Operation(summary = "更新费用", description = "更新费用记录")
    @PutMapping("/expenses/{expenseId}")
    public R<Boolean> updateExpense(@Parameter(description = "费用ID") @PathVariable Long expenseId,
                                     @RequestBody ExpenseDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        boolean success = checklistService.updateExpense(expenseId, userId, dto);
        return success ? R.ok(true) : R.failed("更新失败：费用不存在或无权限");
    }

    @Operation(summary = "删除费用", description = "删除费用记录")
    @DeleteMapping("/expenses/{expenseId}")
    public R<Boolean> removeExpense(@Parameter(description = "费用ID") @PathVariable Long expenseId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        boolean success = checklistService.removeExpense(expenseId, userId);
        return success ? R.ok(true) : R.failed("删除失败：费用不存在或无权限");
    }

    @Operation(summary = "费用总计", description = "获取清单费用总计")
    @GetMapping("/{checklistId}/expenses/total")
    public R<BigDecimal> totalExpense(@Parameter(description = "清单ID") @PathVariable Long checklistId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        return R.ok(checklistService.totalExpense(checklistId, userId));
    }

    @Operation(summary = "设置预算", description = "设置清单预算金额")
    @PutMapping("/{checklistId}/budget")
    public R<Boolean> updateBudget(@Parameter(description = "清单ID") @PathVariable Long checklistId,
                                    @RequestBody BudgetDTO dto) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return R.failed("请先登录");
        }
        boolean success = checklistService.updateBudget(checklistId, userId, dto.getBudget());
        return success ? R.ok(true) : R.failed("设置预算失败：清单不存在或无权限(checklistId=" + checklistId + ", userId=" + userId + ")");
    }

    private Long getCurrentUserId() {
        try {
            return Objects.requireNonNull(SecurityUtils.getUser()).getId();
        } catch (Exception ignored) {
            return null;
        }
    }
}
