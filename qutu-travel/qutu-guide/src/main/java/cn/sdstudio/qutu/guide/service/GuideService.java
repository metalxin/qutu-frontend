package cn.sdstudio.qutu.guide.service;

import cn.sdstudio.qutu.guide.dto.GuideAdminDTO;
import cn.sdstudio.qutu.guide.dto.GuideQueryDTO;
import cn.sdstudio.qutu.guide.vo.GuideAdminDetailVO;
import cn.sdstudio.qutu.guide.vo.GuideAdminVO;
import cn.sdstudio.qutu.guide.vo.GuideListVO;
import cn.sdstudio.qutu.guide.vo.GuideSummaryVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface GuideService {

    List<GuideSummaryVO> relatedGuides(Long spotId, int limit);

    IPage<GuideListVO> pageGuides(Page<GuideListVO> page, GuideQueryDTO query);

    IPage<GuideAdminVO> pageAdminGuides(Page<GuideAdminVO> page, GuideQueryDTO query);

    GuideAdminDetailVO getAdminDetail(Long id);

    boolean saveAdminGuide(GuideAdminDTO dto);

    boolean updateAdminGuide(GuideAdminDTO dto);

    boolean removeAdminGuide(Long id);

    cn.sdstudio.qutu.guide.vo.GuideDetailVO getDetail(Long id);

    java.util.List<cn.sdstudio.qutu.guide.vo.GuideTagVO> getTags(int limit);
}
