package cn.sdstudio.qutu.checklist.service;

import cn.sdstudio.qutu.checklist.dto.ChecklistAdminDTO;
import cn.sdstudio.qutu.checklist.dto.ChecklistCreateDTO;
import cn.sdstudio.qutu.checklist.dto.ChecklistItemDTO;
import cn.sdstudio.qutu.checklist.dto.ChecklistQueryDTO;
import cn.sdstudio.qutu.checklist.dto.ExpenseDTO;
import cn.sdstudio.qutu.checklist.vo.ChecklistAdminDetailVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistAdminVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistDetailVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistListVO;
import cn.sdstudio.qutu.checklist.vo.ExpenseVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.util.List;

public interface ChecklistService {

    // ========== 后台管理方法 ==========
    IPage<ChecklistAdminVO> pageAdminChecklists(Page<ChecklistAdminVO> page, ChecklistQueryDTO query);

    ChecklistAdminDetailVO getAdminDetail(Long id);

    Long createAdminChecklist(ChecklistAdminDTO dto);

    boolean updateAdminChecklist(ChecklistAdminDTO dto);

    boolean removeAdminChecklist(Long id);

    // ========== 用户面方法 ==========
    IPage<ChecklistListVO> pageMyChecklists(Page<ChecklistListVO> page, Long userId);

    ChecklistDetailVO getMyDetail(Long id, Long userId);

    Long createMyChecklist(Long userId, ChecklistCreateDTO dto);

    boolean updateMyChecklist(Long id, Long userId, ChecklistCreateDTO dto);

    boolean removeMyChecklist(Long id, Long userId);

    boolean toggleItem(Long itemId, Long userId, Integer checked);

    Long addItem(Long checklistId, Long userId, ChecklistItemDTO itemDTO);

    boolean removeItem(Long itemId, Long userId);

    // ========== 费用方法 ==========
    List<ExpenseVO> listExpenses(Long checklistId, Long userId);

    Long addExpense(Long checklistId, Long userId, ExpenseDTO dto);

    boolean updateExpense(Long expenseId, Long userId, ExpenseDTO dto);

    boolean removeExpense(Long expenseId, Long userId);

    BigDecimal totalExpense(Long checklistId, Long userId);

    boolean updateBudget(Long checklistId, Long userId, BigDecimal budget);
}
