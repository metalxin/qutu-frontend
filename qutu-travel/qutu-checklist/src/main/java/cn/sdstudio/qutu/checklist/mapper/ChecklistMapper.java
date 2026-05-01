package cn.sdstudio.qutu.checklist.mapper;

import cn.sdstudio.qutu.checklist.dto.ChecklistQueryDTO;
import cn.sdstudio.qutu.checklist.entity.Checklist;
import cn.sdstudio.qutu.checklist.vo.ChecklistAdminDetailVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistAdminVO;
import cn.sdstudio.qutu.checklist.vo.ChecklistListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ChecklistMapper extends BaseMapper<Checklist> {

    IPage<ChecklistAdminVO> selectAdminChecklistPage(Page<ChecklistAdminVO> page, @Param("query") ChecklistQueryDTO query);

    ChecklistAdminDetailVO selectAdminChecklistDetail(@Param("id") Long id);

    IPage<ChecklistListVO> selectMyChecklistPage(Page<ChecklistListVO> page, @Param("userId") Long userId);
}
