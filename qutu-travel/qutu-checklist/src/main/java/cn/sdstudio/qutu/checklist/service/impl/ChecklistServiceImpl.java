package cn.sdstudio.qutu.checklist.service.impl;

import cn.sdstudio.qutu.checklist.dto.ChecklistAdminDTO;
import cn.sdstudio.qutu.checklist.dto.ChecklistCreateDTO;
import cn.sdstudio.qutu.checklist.dto.ChecklistItemDTO;
import cn.sdstudio.qutu.checklist.dto.ChecklistQueryDTO;
import cn.sdstudio.qutu.checklist.dto.ExpenseDTO;
import cn.sdstudio.qutu.checklist.entity.Checklist;
import cn.sdstudio.qutu.checklist.entity.ChecklistItem;
import cn.sdstudio.qutu.checklist.entity.ChecklistExpense;
import cn.sdstudio.qutu.checklist.mapper.ChecklistItemMapper;
import cn.sdstudio.qutu.checklist.mapper.ChecklistMapper;
import cn.sdstudio.qutu.checklist.mapper.ChecklistExpenseMapper;
import cn.sdstudio.qutu.checklist.service.ChecklistService;
import cn.sdstudio.qutu.checklist.vo.ChecklistAdminDetailVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistAdminVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistDetailVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistItemAdminVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistItemVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistListVO;
import cn.sdstudio.qutu.checklist.vo.ExpenseVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {

    private final ChecklistMapper checklistMapper;
    private final ChecklistItemMapper checklistItemMapper;
    private final ChecklistExpenseMapper checklistExpenseMapper;

    @Override
    public IPage<ChecklistAdminVO> pageAdminChecklists(Page<ChecklistAdminVO> page, ChecklistQueryDTO query) {
        return checklistMapper.selectAdminChecklistPage(page, query);
    }

    @Override
    public ChecklistAdminDetailVO getAdminDetail(Long id) {
        ChecklistAdminDetailVO detail = checklistMapper.selectAdminChecklistDetail(id);
        if (detail == null) {
            return null;
        }
        List<ChecklistItem> items = checklistItemMapper.selectList(Wrappers.<ChecklistItem>lambdaQuery()
                .eq(ChecklistItem::getChecklistId, id)
                .orderByAsc(ChecklistItem::getOrderNum));
        List<ChecklistItemAdminVO> itemVOs = new ArrayList<>();
        for (ChecklistItem item : items) {
            ChecklistItemAdminVO vo = new ChecklistItemAdminVO();
            vo.setId(item.getId());
            vo.setCategory(item.getCategory());
            vo.setContent(item.getContent());
            vo.setChecked(item.getChecked());
            vo.setOrderNum(item.getOrderNum());
            itemVOs.add(vo);
        }
        detail.setItems(itemVOs);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAdminChecklist(ChecklistAdminDTO dto) {
        Checklist c = new Checklist();
        c.setUserId(dto.getUserId());
        c.setTitle(dto.getTitle());
        c.setDestination(dto.getDestination());
        c.setDepartDate(dto.getDepartDate());
        c.setReturnDate(dto.getReturnDate());
        c.setRemark(dto.getRemark());
        c.setStatus(dto.getStatus() == null ? 0 : dto.getStatus());
        c.insert();
        saveChecklistItems(c.getId(), dto.getItems());
        return c.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdminChecklist(ChecklistAdminDTO dto) {
        Checklist c = new Checklist();
        c.setId(dto.getId());
        c.selectById();
        c.setTitle(dto.getTitle());
        c.setDestination(dto.getDestination());
        c.setDepartDate(dto.getDepartDate());
        c.setReturnDate(dto.getReturnDate());
        c.setRemark(dto.getRemark());
        if (dto.getStatus() != null) {
            c.setStatus(dto.getStatus());
        }
        // 更新清单项：先删旧再插新
        if (dto.getItems() != null) {
            clearChecklistItems(dto.getId());
            saveChecklistItems(dto.getId(), dto.getItems());
        }
        return c.updateById();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAdminChecklist(Long id) {
        clearChecklistItems(id);
        Checklist c = new Checklist();
        c.setId(id);
        return c.deleteById();
    }

    private void clearChecklistItems(Long checklistId) {
        checklistItemMapper.delete(Wrappers.<ChecklistItem>lambdaQuery()
                .eq(ChecklistItem::getChecklistId, checklistId));
    }

    private void clearChecklistExpenses(Long checklistId) {
        checklistExpenseMapper.delete(Wrappers.<ChecklistExpense>lambdaQuery()
                .eq(ChecklistExpense::getChecklistId, checklistId));
    }

    private void saveChecklistItems(Long checklistId, List<ChecklistItemDTO> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        int checkedCount = 0;
        for (int i = 0; i < items.size(); i++) {
            ChecklistItemDTO dto = items.get(i);
            ChecklistItem item = new ChecklistItem();
            item.setChecklistId(checklistId);
            item.setCategory(dto.getCategory());
            item.setContent(dto.getContent());
            item.setChecked(dto.getChecked() == null ? 0 : dto.getChecked());
            item.setOrderNum(dto.getOrderNum() == null ? i + 1 : dto.getOrderNum());
            checklistItemMapper.insert(item);
            if (item.getChecked() == 1) {
                checkedCount++;
            }
        }
        // 更新主表的统计字段
        Checklist c = new Checklist();
        c.setId(checklistId);
        c.selectById();
        c.setItemsCount(items.size());
        c.setCheckedCount(checkedCount);
        c.updateById();
    }

    // ========== 用户面方法 ==========

    @Override
    public IPage<ChecklistListVO> pageMyChecklists(Page<ChecklistListVO> page, Long userId) {
        return checklistMapper.selectMyChecklistPage(page, userId);
    }

    @Override
    public ChecklistDetailVO getMyDetail(Long id, Long userId) {
        Checklist checklist = new Checklist();
        checklist.setId(id);
        checklist.selectById();
        if (checklist.getUserId() == null || !checklist.getUserId().equals(userId)) {
            return null;
        }
        ChecklistDetailVO detail = new ChecklistDetailVO();
        detail.setId(checklist.getId());
        detail.setTitle(checklist.getTitle());
        detail.setDestination(checklist.getDestination());
        detail.setDepartDate(checklist.getDepartDate());
        detail.setReturnDate(checklist.getReturnDate());
        detail.setRemark(checklist.getRemark());
        detail.setStatus(checklist.getStatus());
        detail.setItemsCount(checklist.getItemsCount());
        detail.setCheckedCount(checklist.getCheckedCount());
        detail.setBudget(checklist.getBudget());
        detail.setCreatedAt(checklist.getCreatedAt());
        detail.setUpdatedAt(checklist.getUpdatedAt());

        List<ChecklistItem> items = checklistItemMapper.selectList(Wrappers.<ChecklistItem>lambdaQuery()
                .eq(ChecklistItem::getChecklistId, id)
                .orderByAsc(ChecklistItem::getOrderNum));
        List<ChecklistItemVO> itemVOs = new ArrayList<>();
        for (ChecklistItem item : items) {
            ChecklistItemVO vo = new ChecklistItemVO();
            vo.setId(item.getId());
            vo.setCategory(item.getCategory());
            vo.setContent(item.getContent());
            vo.setChecked(item.getChecked());
            vo.setOrderNum(item.getOrderNum());
            itemVOs.add(vo);
        }
        detail.setItems(itemVOs);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMyChecklist(Long userId, ChecklistCreateDTO dto) {
        Checklist c = new Checklist();
        c.setUserId(userId);
        c.setTitle(dto.getTitle());
        c.setDestination(dto.getDestination());
        c.setDepartDate(dto.getDepartDate());
        c.setReturnDate(dto.getReturnDate());
        c.setRemark(dto.getRemark());
        c.setStatus(0);
        c.insert();
        saveChecklistItems(c.getId(), dto.getItems());
        return c.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMyChecklist(Long id, Long userId, ChecklistCreateDTO dto) {
        Checklist c = new Checklist();
        c.setId(id);
        c.selectById();
        if (c.getUserId() == null || !c.getUserId().equals(userId)) {
            return false;
        }
        if (dto.getTitle() != null) {
            c.setTitle(dto.getTitle());
        }
        if (dto.getDestination() != null) {
            c.setDestination(dto.getDestination());
        }
        if (dto.getDepartDate() != null) {
            c.setDepartDate(dto.getDepartDate());
        }
        if (dto.getReturnDate() != null) {
            c.setReturnDate(dto.getReturnDate());
        }
        if (dto.getRemark() != null) {
            c.setRemark(dto.getRemark());
        }
        // 更新清单项：先删旧再插新
        if (dto.getItems() != null) {
            clearChecklistItems(id);
            saveChecklistItems(id, dto.getItems());
        }
        return c.updateById();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeMyChecklist(Long id, Long userId) {
        Checklist c = new Checklist();
        c.setId(id);
        c.selectById();
        if (c.getUserId() == null || !c.getUserId().equals(userId)) {
            return false;
        }
        clearChecklistItems(id);
        clearChecklistExpenses(id);
        return c.deleteById();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleItem(Long itemId, Long userId, Integer checked) {
        ChecklistItem item = checklistItemMapper.selectById(itemId);
        if (item == null) {
            return false;
        }
        // 校验清单归属
        Checklist c = new Checklist();
        c.setId(item.getChecklistId());
        c.selectById();
        if (c.getUserId() == null || !c.getUserId().equals(userId)) {
            return false;
        }
        item.setChecked(checked);
        checklistItemMapper.updateById(item);
        // 重新统计
        recountChecklist(item.getChecklistId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addItem(Long checklistId, Long userId, ChecklistItemDTO itemDTO) {
        Checklist c = new Checklist();
        c.setId(checklistId);
        c.selectById();
        if (c.getUserId() == null || !c.getUserId().equals(userId)) {
            return null;
        }
        ChecklistItem item = new ChecklistItem();
        item.setChecklistId(checklistId);
        item.setCategory(itemDTO.getCategory());
        item.setContent(itemDTO.getContent());
        item.setChecked(itemDTO.getChecked() == null ? 0 : itemDTO.getChecked());
        // 排序取当前最大orderNum+1
        List<ChecklistItem> existingItems = checklistItemMapper.selectList(Wrappers.<ChecklistItem>lambdaQuery()
                .eq(ChecklistItem::getChecklistId, checklistId)
                .orderByDesc(ChecklistItem::getOrderNum)
                .last("LIMIT 1"));
        int maxOrder = (existingItems != null && !existingItems.isEmpty())
                ? existingItems.get(0).getOrderNum() : 0;
        item.setOrderNum(itemDTO.getOrderNum() == null ? maxOrder + 1 : itemDTO.getOrderNum());
        checklistItemMapper.insert(item);
        recountChecklist(checklistId);
        return item.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeItem(Long itemId, Long userId) {
        ChecklistItem item = checklistItemMapper.selectById(itemId);
        if (item == null) {
            return false;
        }
        Checklist c = new Checklist();
        c.setId(item.getChecklistId());
        c.selectById();
        if (c.getUserId() == null || !c.getUserId().equals(userId)) {
            return false;
        }
        checklistItemMapper.deleteById(itemId);
        recountChecklist(item.getChecklistId());
        return true;
    }

    private void recountChecklist(Long checklistId) {
        List<ChecklistItem> allItems = checklistItemMapper.selectList(Wrappers.<ChecklistItem>lambdaQuery()
                .eq(ChecklistItem::getChecklistId, checklistId));
        int checked = (int) allItems.stream().filter(i -> i.getChecked() != null && i.getChecked() == 1).count();
        Checklist c = new Checklist();
        c.setId(checklistId);
        c.selectById();
        c.setItemsCount(allItems.size());
        c.setCheckedCount(checked);
        // 自动判断状态
        if (allItems.size() > 0 && checked == allItems.size()) {
            c.setStatus(2); // 已完成
        } else if (checked > 0) {
            c.setStatus(1); // 进行中
        } else {
            c.setStatus(0); // 未开始
        }
        c.updateById();
    }

    // ========== 费用方法 ==========

    @Override
    public List<ExpenseVO> listExpenses(Long checklistId, Long userId) {
        if (!checkOwnership(checklistId, userId)) {
            return new ArrayList<>();
        }
        List<ChecklistExpense> expenses = checklistExpenseMapper.selectList(Wrappers.<ChecklistExpense>lambdaQuery()
                .eq(ChecklistExpense::getChecklistId, checklistId)
                .orderByDesc(ChecklistExpense::getCreatedAt));
        List<ExpenseVO> vos = new ArrayList<>();
        for (ChecklistExpense e : expenses) {
            ExpenseVO vo = new ExpenseVO();
            vo.setId(e.getId());
            vo.setChecklistId(e.getChecklistId());
            vo.setCategory(e.getCategory());
            vo.setAmount(e.getAmount());
            vo.setNote(e.getNote());
            vo.setExpenseDate(e.getExpenseDate() != null ? e.getExpenseDate().toString() : null);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addExpense(Long checklistId, Long userId, ExpenseDTO dto) {
        if (!checkOwnership(checklistId, userId)) {
            return null;
        }
        ChecklistExpense expense = new ChecklistExpense();
        expense.setChecklistId(checklistId);
        expense.setCategory(dto.getCategory());
        expense.setAmount(dto.getAmount());
        expense.setNote(dto.getNote());
        if (dto.getExpenseDate() != null && !dto.getExpenseDate().isBlank()) {
            try {
                expense.setExpenseDate(LocalDate.parse(dto.getExpenseDate()));
            } catch (Exception ignored) {
                expense.setExpenseDate(LocalDate.now());
            }
        } else {
            expense.setExpenseDate(LocalDate.now());
        }
        checklistExpenseMapper.insert(expense);
        return expense.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateExpense(Long expenseId, Long userId, ExpenseDTO dto) {
        ChecklistExpense expense = checklistExpenseMapper.selectById(expenseId);
        if (expense == null) {
            return false;
        }
        if (!checkOwnership(expense.getChecklistId(), userId)) {
            return false;
        }
        if (dto.getCategory() != null) {
            expense.setCategory(dto.getCategory());
        }
        if (dto.getAmount() != null) {
            expense.setAmount(dto.getAmount());
        }
        if (dto.getNote() != null) {
            expense.setNote(dto.getNote());
        }
        if (dto.getExpenseDate() != null && !dto.getExpenseDate().isBlank()) {
            try {
                expense.setExpenseDate(LocalDate.parse(dto.getExpenseDate()));
            } catch (Exception ignored) {
            }
        }
        return checklistExpenseMapper.updateById(expense) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeExpense(Long expenseId, Long userId) {
        ChecklistExpense expense = checklistExpenseMapper.selectById(expenseId);
        if (expense == null) {
            return false;
        }
        if (!checkOwnership(expense.getChecklistId(), userId)) {
            return false;
        }
        return checklistExpenseMapper.deleteById(expenseId) > 0;
    }

    @Override
    public BigDecimal totalExpense(Long checklistId, Long userId) {
        if (!checkOwnership(checklistId, userId)) {
            return BigDecimal.ZERO;
        }
        List<ChecklistExpense> expenses = checklistExpenseMapper.selectList(Wrappers.<ChecklistExpense>lambdaQuery()
                .eq(ChecklistExpense::getChecklistId, checklistId));
        return expenses.stream()
                .map(ChecklistExpense::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateBudget(Long checklistId, Long userId, BigDecimal budget) {
        Checklist c = new Checklist();
        c.setId(checklistId);
        c.selectById();
        if (c.getUserId() == null || !c.getUserId().equals(userId)) {
            return false;
        }
        c.setBudget(budget);
        return c.updateById();
    }

    private boolean checkOwnership(Long checklistId, Long userId) {
        Checklist c = new Checklist();
        c.setId(checklistId);
        c.selectById();
        return c.getUserId() != null && c.getUserId().equals(userId);
    }
}
