package cn.sdstudio.qutu.story.service.impl;

import cn.sdstudio.qutu.story.dto.StoryAdminDTO;
import cn.sdstudio.qutu.story.dto.StoryCreateDTO;
import cn.sdstudio.qutu.story.dto.StoryQueryDTO;
import cn.sdstudio.qutu.story.entity.Story;
import cn.sdstudio.qutu.story.entity.StoryImage;
import cn.sdstudio.qutu.story.mapper.StoryImageMapper;
import cn.sdstudio.qutu.story.mapper.StoryMapper;
import cn.sdstudio.qutu.story.service.StoryService;
import cn.sdstudio.qutu.story.vo.StoryAdminDetailVO;
import cn.sdstudio.qutu.story.vo.StoryAdminVO;
import cn.sdstudio.qutu.story.vo.StoryDetailVO;
import cn.sdstudio.qutu.story.vo.StoryListVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoryServiceImpl implements StoryService {

    private final StoryMapper storyMapper;
    private final StoryImageMapper storyImageMapper;

    @Override
    public IPage<StoryListVO> pageStories(Page<StoryListVO> page, Long userId, LocalDate recordDate) {
        return storyMapper.selectStoryPage(page, userId, recordDate);
    }

    @Override
    public StoryDetailVO getDetail(Long id, Long userId) {
        StoryDetailVO detail = storyMapper.selectStoryDetail(id, userId);
        if (detail == null) {
            return null;
        }
        List<String> images = storyImageMapper.selectImages(id);
        detail.setImages(images);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStory(Long userId, StoryCreateDTO dto) {
        Story s = new Story();
        s.setUserId(userId);
        s.setContent(dto.getContent());
        s.setLocationName(dto.getLocationName());
        s.setLng(dto.getLng());
        s.setLat(dto.getLat());
        s.setRecordDate(dto.getRecordDate());
        s.setMood(dto.getMood());
        s.setWeather(dto.getWeather());
        s.setSpotId(dto.getSpotId());
        int count = dto.getImages() == null ? 0 : dto.getImages().size();
        s.setImagesCount(count);
        s.setVisibility(1);
        s.insert();
        if (count > 0) {
            int i = 1;
            for (String url : dto.getImages()) {
                StoryImage img = new StoryImage();
                img.setStoryId(s.getId());
                img.setUrl(url);
                img.setOrderNum(i++);
                storyImageMapper.insert(img);
            }
        }
        return s.getId();
    }

    @Override
    public boolean deleteStory(Long id, Long userId) {
        Story story = new Story();
        story.setId(id);
        story.setUserId(userId);
        return story.deleteById();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStory(Long id, Long userId, StoryCreateDTO dto) {
        Story existing = new Story();
        existing.setId(id);
        existing = existing.selectById();
        if (existing == null || !existing.getUserId().equals(userId)) {
            return false;
        }
        if (dto.getContent() != null) {
            existing.setContent(dto.getContent());
        }
        if (dto.getLocationName() != null) {
            existing.setLocationName(dto.getLocationName());
        }
        if (dto.getLng() != null) {
            existing.setLng(dto.getLng());
        }
        if (dto.getLat() != null) {
            existing.setLat(dto.getLat());
        }
        if (dto.getRecordDate() != null) {
            existing.setRecordDate(dto.getRecordDate());
        }
        if (dto.getMood() != null) {
            existing.setMood(dto.getMood());
        }
        if (dto.getWeather() != null) {
            existing.setWeather(dto.getWeather());
        }
        if (dto.getSpotId() != null) {
            existing.setSpotId(dto.getSpotId());
        }
        // 更新图片：先删旧的再插新的
        if (dto.getImages() != null) {
            storyImageMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StoryImage>().eq(StoryImage::getStoryId, id));
            int i = 1;
            for (String url : dto.getImages()) {
                StoryImage img = new StoryImage();
                img.setStoryId(id);
                img.setUrl(url);
                img.setOrderNum(i++);
                storyImageMapper.insert(img);
            }
            existing.setImagesCount(dto.getImages().size());
        }
        return existing.updateById();
    }

    // ===== 后台管理 =====

    @Override
    public IPage<StoryAdminVO> pageAdminStories(Page<StoryAdminVO> page, StoryQueryDTO query) {
        return storyMapper.selectAdminStoryPage(page, query);
    }

    @Override
    public StoryAdminDetailVO getAdminDetail(Long id) {
        StoryAdminDetailVO detail = storyMapper.selectAdminStoryDetail(id);
        if (detail == null) {
            return null;
        }
        List<String> images = storyImageMapper.selectImages(id);
        detail.setImages(images);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAdminStory(StoryAdminDTO dto) {
        Story s = new Story();
        s.setUserId(dto.getUserId());
        s.setContent(dto.getContent());
        s.setLocationName(dto.getLocationName());
        s.setLng(dto.getLng());
        s.setLat(dto.getLat());
        s.setRecordDate(dto.getRecordDate());
        s.setMood(dto.getMood());
        s.setWeather(dto.getWeather());
        s.setVisibility(dto.getVisibility() != null ? dto.getVisibility() : 3);
        s.setSpotId(dto.getSpotId());
        int count = dto.getImages() == null ? 0 : dto.getImages().size();
        s.setImagesCount(count);
        s.insert();
        if (count > 0) {
            int i = 1;
            for (String url : dto.getImages()) {
                StoryImage img = new StoryImage();
                img.setStoryId(s.getId());
                img.setUrl(url);
                img.setOrderNum(i++);
                storyImageMapper.insert(img);
            }
        }
        return s.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdminStory(StoryAdminDTO dto) {
        Story s = new Story();
        s.setId(dto.getId());
        s.selectById();
        s.setContent(dto.getContent());
        s.setLocationName(dto.getLocationName());
        s.setLng(dto.getLng());
        s.setLat(dto.getLat());
        s.setRecordDate(dto.getRecordDate());
        s.setMood(dto.getMood());
        s.setWeather(dto.getWeather());
        if (dto.getVisibility() != null) {
            s.setVisibility(dto.getVisibility());
        }
        s.setSpotId(dto.getSpotId());
        // 更新图片：先删旧的再插新的
        if (dto.getImages() != null) {
            storyImageMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StoryImage>().eq(StoryImage::getStoryId, dto.getId()));
            int i = 1;
            for (String url : dto.getImages()) {
                StoryImage img = new StoryImage();
                img.setStoryId(dto.getId());
                img.setUrl(url);
                img.setOrderNum(i++);
                storyImageMapper.insert(img);
            }
            s.setImagesCount(dto.getImages().size());
        }
        return s.updateById();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAdminStory(Long id) {
        // 删除关联图片
        storyImageMapper.delete(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<StoryImage>().eq(StoryImage::getStoryId, id));
        Story story = new Story();
        story.setId(id);
        return story.deleteById();
    }
}
