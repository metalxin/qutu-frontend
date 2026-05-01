package cn.sdstudio.qutu.story.mapper;

import cn.sdstudio.qutu.story.dto.StoryQueryDTO;
import cn.sdstudio.qutu.story.entity.Story;
import cn.sdstudio.qutu.story.vo.StoryAdminDetailVO;
import cn.sdstudio.qutu.story.vo.StoryAdminVO;
import cn.sdstudio.qutu.story.vo.StoryDetailVO;
import cn.sdstudio.qutu.story.vo.StoryListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StoryMapper extends BaseMapper<Story> {

    IPage<StoryListVO> selectStoryPage(Page<StoryListVO> page, @Param("userId") Long userId, @Param("recordDate") java.time.LocalDate recordDate);

    StoryDetailVO selectStoryDetail(@Param("id") Long id, @Param("userId") Long userId);

    // ===== 后台管理 =====

    IPage<StoryAdminVO> selectAdminStoryPage(Page<StoryAdminVO> page, @Param("query") StoryQueryDTO query);

    StoryAdminDetailVO selectAdminStoryDetail(@Param("id") Long id);
}
