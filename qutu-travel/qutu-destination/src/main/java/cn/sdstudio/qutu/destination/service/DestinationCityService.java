package cn.sdstudio.qutu.destination.service;

import cn.sdstudio.qutu.destination.dto.CityQueryDTO;
import cn.sdstudio.qutu.destination.entity.DestinationCity;
import cn.sdstudio.qutu.destination.vo.DestinationVO;
import cn.sdstudio.qutu.destination.vo.RegionGroupVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 目的地城市Service
 *
 * @author qutu
 * @date 2026-02-02
 */
public interface DestinationCityService extends IService<DestinationCity> {

    /**
     * 分页查询目的地列表
     */
    IPage<DestinationVO> pageDestinations(Page<DestinationVO> page, CityQueryDTO query);

    /**
     * 获取目的地详情
     */
    DestinationVO getDestinationDetail(Long id);

    /**
     * 获取热门城市列表
     */
    List<String> getHotCities(int limit);

    /**
     * 获取地区分组
     */
    List<RegionGroupVO> getRegionGroups();

    /**
     * 增加浏览量
     */
    void incrementViewCount(Long id);

    /**
     * 根据省份获取城市列表
     */
    List<DestinationVO> getCitiesByProvince(String province);

    /**
     * 搜索目的地
     */
    List<DestinationVO> searchDestinations(String keyword, int limit);

    /**
     * 重新统计指定城市的景点数量
     */
    void refreshSpotsCount(Long cityId);

    /**
     * 重新统计所有城市的景点数量
     */
    void refreshAllSpotsCount();
}
