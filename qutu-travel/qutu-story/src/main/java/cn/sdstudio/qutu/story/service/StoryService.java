package cn.sdstudio.qutu.story.service;

import cn.sdstudio.qutu.story.dto.StoryAdminDTO;
import cn.sdstudio.qutu.story.dto.StoryCreateDTO;
import cn.sdstudio.qutu.story.dto.StoryQueryDTO;
import cn.sdstudio.qutu.story.vo.StoryAdminDetailVO;
import cn.sdstudio.qutu.story.vo.StoryAdminVO;
import cn.sdstudio.qutu.story.vo.StoryDetailVO;
import cn.sdstudio.qutu.story.vo.StoryListVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.time.LocalDate;

public interface StoryService {

    IPage<StoryListVO> pageStories(Page<StoryListVO> page, Long userId, LocalDate recordDate);

    StoryDetailVO getDetail(Long id, Long userId);

    Long createStory(Long userId, StoryCreateDTO dto);

    boolean updateStory(Long id, Long userId, StoryCreateDTO dto);

    boolean deleteStory(Long id, Long userId);

    // ===== 后台管理 =====

    IPage<StoryAdminVO> pageAdminStories(Page<StoryAdminVO> page, StoryQueryDTO query);

    StoryAdminDetailVO getAdminDetail(Long id);

    Long createAdminStory(StoryAdminDTO dto);

    boolean updateAdminStory(StoryAdminDTO dto);

    boolean removeAdminStory(Long id);
}
