package cn.sdstudio.qutu.guide.service.impl;

import cn.sdstudio.qutu.guide.dto.GuideAdminDTO;
import cn.sdstudio.qutu.guide.dto.GuideDayDTO;
import cn.sdstudio.qutu.guide.dto.GuideDayItemDTO;
import cn.sdstudio.qutu.guide.entity.Guide;
import cn.sdstudio.qutu.guide.entity.GuideDay;
import cn.sdstudio.qutu.guide.entity.GuideDayItem;
import cn.sdstudio.qutu.guide.mapper.GuideMapper;
import cn.sdstudio.qutu.guide.mapper.GuideDayItemMapper;
import cn.sdstudio.qutu.guide.mapper.GuideDayMapper;
import cn.sdstudio.qutu.guide.service.GuideService;
import cn.sdstudio.qutu.guide.dto.GuideQueryDTO;
import cn.sdstudio.qutu.guide.vo.GuideAdminDetailVO;
import cn.sdstudio.qutu.guide.vo.GuideAdminVO;
import cn.sdstudio.qutu.guide.vo.GuideListVO;
import cn.sdstudio.qutu.guide.vo.GuideDayVO;
import cn.sdstudio.qutu.guide.vo.GuideDayItemVO;
import cn.sdstudio.qutu.guide.vo.GuideSummaryVO;
import cn.sdstudio.qutu.guide.vo.GuideDetailVO;
import cn.sdstudio.qutu.guide.vo.GuideTagVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuideServiceImpl implements GuideService {

    private final GuideMapper guideMapper;
    private final GuideDayMapper guideDayMapper;
    private final GuideDayItemMapper guideDayItemMapper;

    @Override
    public List<GuideSummaryVO> relatedGuides(Long spotId, int limit) {
        return guideMapper.selectRelatedBySpotId(spotId, limit);
    }

    @Override
    public IPage<GuideListVO> pageGuides(Page<GuideListVO> page, GuideQueryDTO query) {
        return guideMapper.selectGuidePage(page, query);
    }

    @Override
    public IPage<GuideAdminVO> pageAdminGuides(Page<GuideAdminVO> page, GuideQueryDTO query) {
        return guideMapper.selectGuideAdminPage(page, query);
    }

    @Override
    public GuideAdminDetailVO getAdminDetail(Long id) {
        GuideAdminDetailVO detail = guideMapper.selectGuideAdminDetail(id);
        if (detail == null) {
            return null;
        }
        List<GuideDay> days = guideDayMapper.selectList(Wrappers.<GuideDay>lambdaQuery()
                .eq(GuideDay::getGuideId, id)
                .orderByAsc(GuideDay::getDayIndex));
        List<cn.sdstudio.qutu.guide.vo.GuideDayAdminVO> itinerary = new ArrayList<>();
        for (GuideDay day : days) {
            cn.sdstudio.qutu.guide.vo.GuideDayAdminVO dayVO = new cn.sdstudio.qutu.guide.vo.GuideDayAdminVO();
            dayVO.setId(day.getId());
            dayVO.setDayIndex(day.getDayIndex());
            dayVO.setTitle(day.getTitle());
            dayVO.setSummary(day.getSummary());
            List<GuideDayItem> items = guideDayItemMapper.selectList(Wrappers.<GuideDayItem>lambdaQuery()
                    .eq(GuideDayItem::getGuideId, id)
                    .eq(GuideDayItem::getDayIndex, day.getDayIndex())
                    .orderByAsc(GuideDayItem::getOrderNum));
            List<cn.sdstudio.qutu.guide.vo.GuideDayItemAdminVO> itemVOs = new ArrayList<>();
            for (GuideDayItem item : items) {
                cn.sdstudio.qutu.guide.vo.GuideDayItemAdminVO itemVO = new cn.sdstudio.qutu.guide.vo.GuideDayItemAdminVO();
                itemVO.setId(item.getId());
                itemVO.setType(item.getType());
                itemVO.setTitle(item.getTitle());
                itemVO.setContent(item.getContent());
                itemVO.setSpotId(item.getSpotId());
                itemVO.setOrderNum(item.getOrderNum());
                itemVOs.add(itemVO);
            }
            dayVO.setItems(itemVOs);
            itinerary.add(dayVO);
        }
        detail.setItinerary(itinerary);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveAdminGuide(GuideAdminDTO dto) {
        Guide guide = toGuide(dto);
        guideMapper.insert(guide);
        saveGuideItinerary(guide.getId(), dto.getItinerary());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdminGuide(GuideAdminDTO dto) {
        Guide guide = toGuide(dto);
        guideMapper.updateById(guide);
        clearGuideItinerary(guide.getId());
        saveGuideItinerary(guide.getId(), dto.getItinerary());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAdminGuide(Long id) {
        guideMapper.deleteById(id);
        clearGuideItinerary(id);
        return true;
    }

    @Override
    public GuideDetailVO getDetail(Long id) {
        GuideDetailVO detail = guideMapper.selectGuideDetail(id);
        if (detail == null) {
            return null;
        }
        List<GuideDayVO> days = guideMapper.selectGuideDays(id);
        if (days != null && !days.isEmpty()) {
            Map<Integer, List<GuideDayItemVO>> itemMap = days.stream()
                    .map(GuideDayVO::getDayIndex)
                    .distinct()
                    .collect(Collectors.toMap(d -> d, d -> guideMapper.selectGuideDayItems(id, d)));
            for (GuideDayVO day : days) {
                day.setItems(itemMap.getOrDefault(day.getDayIndex(), List.of()));
            }
        }
        detail.setItinerary(days);
        return detail;
    }

    @Override
    public List<GuideTagVO> getTags(int limit) {
        return guideMapper.selectGuideTags(limit);
    }

    private Guide toGuide(GuideAdminDTO dto) {
        Guide guide = new Guide();
        guide.setId(dto.getId());
        guide.setTitle(dto.getTitle());
        guide.setCoverUrl(dto.getCoverUrl());
        guide.setSummary(dto.getSummary());
        guide.setDays(dto.getDays() == null ? 1 : dto.getDays());
        guide.setNights(dto.getNights() == null ? 0 : dto.getNights());
        guide.setTag(dto.getTag());
        guide.setLikeCount(dto.getLikeCount() == null ? 0 : dto.getLikeCount());
        guide.setViewCount(dto.getViewCount() == null ? 0L : dto.getViewCount());
        guide.setCityId(dto.getCityId());
        guide.setCityName(dto.getCityName());
        guide.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        return guide;
    }

    private void clearGuideItinerary(Long guideId) {
        guideDayItemMapper.delete(Wrappers.<GuideDayItem>lambdaQuery().eq(GuideDayItem::getGuideId, guideId));
        guideDayMapper.delete(Wrappers.<GuideDay>lambdaQuery().eq(GuideDay::getGuideId, guideId));
        guideMapper.deleteGuideSpotMaps(guideId);
    }

    private void saveGuideItinerary(Long guideId, List<GuideDayDTO> itinerary) {
        if (itinerary == null || itinerary.isEmpty()) {
            return;
        }
        Set<Long> spotIds = new LinkedHashSet<>();
        for (int dayIndex = 0; dayIndex < itinerary.size(); dayIndex++) {
            GuideDayDTO dayDTO = itinerary.get(dayIndex);
            int realDayIndex = dayDTO.getDayIndex() == null ? dayIndex + 1 : dayDTO.getDayIndex();

            GuideDay day = new GuideDay();
            day.setGuideId(guideId);
            day.setDayIndex(realDayIndex);
            day.setTitle(dayDTO.getTitle());
            day.setSummary(dayDTO.getSummary());
            guideDayMapper.insert(day);

            List<GuideDayItemDTO> items = dayDTO.getItems();
            if (items == null || items.isEmpty()) {
                continue;
            }
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                GuideDayItemDTO itemDTO = items.get(itemIndex);
                GuideDayItem item = new GuideDayItem();
                item.setGuideId(guideId);
                item.setDayIndex(realDayIndex);
                item.setType(itemDTO.getType());
                item.setTitle(itemDTO.getTitle());
                item.setContent(itemDTO.getContent());
                item.setSpotId(itemDTO.getSpotId());
                item.setOrderNum(itemDTO.getOrderNum() == null ? itemIndex + 1 : itemDTO.getOrderNum());
                guideDayItemMapper.insert(item);
                if ("spot".equalsIgnoreCase(item.getType()) && item.getSpotId() != null) {
                    spotIds.add(item.getSpotId());
                }
            }
        }
        if (!spotIds.isEmpty()) {
            guideMapper.insertGuideSpotMaps(guideId, new ArrayList<>(spotIds));
        }
    }
}
