package cn.sdstudio.qutu.guide.mapper;

import cn.sdstudio.qutu.guide.entity.Guide;
import cn.sdstudio.qutu.guide.dto.GuideQueryDTO;
import cn.sdstudio.qutu.guide.vo.GuideAdminDetailVO;
import cn.sdstudio.qutu.guide.vo.GuideAdminVO;
import cn.sdstudio.qutu.guide.vo.GuideDayItemVO;
import cn.sdstudio.qutu.guide.vo.GuideDayVO;
import cn.sdstudio.qutu.guide.vo.GuideDetailVO;
import cn.sdstudio.qutu.guide.vo.GuideListVO;
import cn.sdstudio.qutu.guide.vo.GuideSummaryVO;
import cn.sdstudio.qutu.guide.vo.GuideTagVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GuideMapper extends BaseMapper<Guide> {

    List<GuideSummaryVO> selectRelatedBySpotId(@Param("spotId") Long spotId, @Param("limit") int limit);

    IPage<GuideListVO> selectGuidePage(Page<GuideListVO> page, @Param("query") GuideQueryDTO query);

    GuideDetailVO selectGuideDetail(@Param("id") Long id);

    List<GuideDayVO> selectGuideDays(@Param("guideId") Long guideId);

    List<GuideDayItemVO> selectGuideDayItems(@Param("guideId") Long guideId, @Param("dayIndex") Integer dayIndex);

    List<GuideTagVO> selectGuideTags(@Param("limit") int limit);

    IPage<GuideAdminVO> selectGuideAdminPage(Page<GuideAdminVO> page, @Param("query") GuideQueryDTO query);

    GuideAdminDetailVO selectGuideAdminDetail(@Param("id") Long id);

    void deleteGuideSpotMaps(@Param("guideId") Long guideId);

    void insertGuideSpotMaps(@Param("guideId") Long guideId, @Param("spotIds") List<Long> spotIds);
}
