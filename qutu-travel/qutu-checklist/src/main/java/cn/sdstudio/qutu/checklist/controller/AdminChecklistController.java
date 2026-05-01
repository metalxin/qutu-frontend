package cn.sdstudio.qutu.checklist.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.log.annotation.SysLog;
import cn.sdstudio.qutu.common.security.annotation.HasPermission;
import cn.sdstudio.qutu.checklist.dto.ChecklistAdminDTO;
import cn.sdstudio.qutu.checklist.dto.ChecklistQueryDTO;
import cn.sdstudio.qutu.checklist.service.ChecklistService;
import cn.sdstudio.qutu.checklist.vo.ChecklistAdminDetailVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistAdminVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * 行前清单后台管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/checklist-admin")
@Tag(name = "清单后台", description = "行前清单后台管理接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class AdminChecklistController {

    private final ChecklistService checklistService;

    @Operation(summary = "清单分页", description = "后台分页查询所有用户清单")
    @GetMapping("/page")
    public R<?> pageChecklists(@ParameterObject Page<ChecklistAdminVO> page,
                                @ParameterObject ChecklistQueryDTO query) {
        return R.ok(checklistService.pageAdminChecklists(page, query));
    }

    @Operation(summary = "清单详情", description = "获取清单详情（含清单项）")
    @GetMapping("/{id}")
    public R<ChecklistAdminDetailVO> getChecklist(@Parameter(description = "清单ID") @PathVariable Long id) {
        return R.ok(checklistService.getAdminDetail(id));
    }

    @Operation(summary = "新增清单", description = "后台新增清单")
    @SysLog("新增清单")
    @PostMapping
    @HasPermission("checklist_add")
    public R<Long> saveChecklist(@RequestBody ChecklistAdminDTO dto) {
        return R.ok(checklistService.createAdminChecklist(dto));
    }

    @Operation(summary = "修改清单", description = "后台修改清单")
    @SysLog("修改清单")
    @PutMapping
    @HasPermission("checklist_edit")
    public R<Boolean> updateChecklist(@RequestBody ChecklistAdminDTO dto) {
        return R.ok(checklistService.updateAdminChecklist(dto));
    }

    @Operation(summary = "删除清单", description = "后台删除清单")
    @SysLog("删除清单")
    @DeleteMapping("/{id}")
    @HasPermission("checklist_del")
    public R<Boolean> deleteChecklist(@Parameter(description = "清单ID") @PathVariable Long id) {
        return R.ok(checklistService.removeAdminChecklist(id));
    }
}
