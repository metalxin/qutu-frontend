package cn.sdstudio.qutu.story.mapper;

import cn.sdstudio.qutu.story.entity.StoryImage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StoryImageMapper extends BaseMapper<StoryImage> {

    @Select("SELECT url FROM t_story_image WHERE story_id = #{storyId} ORDER BY order_num ASC")
    List<String> selectImages(@Param("storyId") Long storyId);
}
