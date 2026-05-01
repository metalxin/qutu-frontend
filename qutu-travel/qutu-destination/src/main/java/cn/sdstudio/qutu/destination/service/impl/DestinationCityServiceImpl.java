package cn.sdstudio.qutu.destination.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.sdstudio.qutu.destination.dto.CityQueryDTO;
import cn.sdstudio.qutu.destination.entity.DestinationCity;
import cn.sdstudio.qutu.destination.mapper.DestinationCityMapper;
import cn.sdstudio.qutu.destination.service.DestinationCityService;
import cn.sdstudio.qutu.destination.vo.DestinationVO;
import cn.sdstudio.qutu.destination.vo.RegionGroupVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 目的地城市Service实现
 *
 * @author qutu
 * @date 2026-02-02
 */
@Service
@RequiredArgsConstructor
public class DestinationCityServiceImpl extends ServiceImpl<DestinationCityMapper, DestinationCity> 
        implements DestinationCityService {

    // 地区分组配置
    private static final Map<String, List<String>> REGION_MAP = new LinkedHashMap<>();
    
    static {
        REGION_MAP.put("北方地区", Arrays.asList("北京", "天津", "河北", "山西", "内蒙古", "辽宁", "吉林", "黑龙江", "山东"));
        REGION_MAP.put("南方地区", Arrays.asList("湖南", "广东", "广西", "海南", "福建"));
        REGION_MAP.put("西部地区", Arrays.asList("四川", "云南", "贵州", "西藏", "新疆", "青海", "甘肃", "宁夏", "陕西", "重庆"));
        REGION_MAP.put("东部地区", Arrays.asList("上海", "江苏", "浙江", "安徽", "江西", "湖北", "河南"));
    }

    // 渐变色配置
    private static final List<String> GRADIENTS = Arrays.asList(
            "linear-gradient(135deg, #E53935 0%, #FF7043 100%)",
            "linear-gradient(135deg, #5C6BC0 0%, #7986CB 100%)",
            "linear-gradient(135deg, #1E88E5 0%, #42A5F5 100%)",
            "linear-gradient(135deg, #43A047 0%, #66BB6A 100%)",
            "linear-gradient(135deg, #00ACC1 0%, #26C6DA 100%)",
            "linear-gradient(135deg, #8D6E63 0%, #A1887F 100%)",
            "linear-gradient(135deg, #F4511E 0%, #FF7043 100%)",
            "linear-gradient(135deg, #7E57C2 0%, #9575CD 100%)",
            "linear-gradient(135deg, #039BE5 0%, #4FC3F7 100%)",
            "linear-gradient(135deg, #FFB300 0%, #FFD54F 100%)"
    );

    @Override
    public IPage<DestinationVO> pageDestinations(Page<DestinationVO> page, CityQueryDTO query) {
        LambdaQueryWrapper<DestinationCity> wrapper = Wrappers.lambdaQuery();
        
        // 构建查询条件
        wrapper.like(StrUtil.isNotBlank(query.getName()), DestinationCity::getName, query.getName());
        wrapper.eq(StrUtil.isNotBlank(query.getProvince()), DestinationCity::getProvince, query.getProvince());
        wrapper.eq(query.getIsHot() != null, DestinationCity::getIsHot, query.getIsHot());
        wrapper.ge(query.getMinRating() != null, DestinationCity::getAvgRating, query.getMinRating());
        
        // 关键词搜索
        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(DestinationCity::getName, query.getKeyword())
                    .or().like(DestinationCity::getSubtitle, query.getKeyword())
                    .or().like(DestinationCity::getIntro, query.getKeyword()));
        }
        
        // 地区筛选
        if (StrUtil.isNotBlank(query.getRegion()) && REGION_MAP.containsKey(query.getRegion())) {
            wrapper.in(DestinationCity::getProvince, REGION_MAP.get(query.getRegion()));
        }
        
        wrapper.orderByDesc(DestinationCity::getHotScore);
        
        // 查询并转换
        Page<DestinationCity> entityPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<DestinationCity> resultPage = this.page(entityPage, wrapper);
        
        return resultPage.convert(this::convertToVO);
    }

    @Override
    public DestinationVO getDestinationDetail(Long id) {
        DestinationCity city = this.getById(id);
        if (city == null) {
            return null;
        }
        return convertToVO(city);
    }

    @Override
    public List<String> getHotCities(int limit) {
        return baseMapper.selectHotCities(limit);
    }

    @Override
    public List<RegionGroupVO> getRegionGroups() {
        return REGION_MAP.entrySet().stream()
                .map(entry -> {
                    RegionGroupVO vo = new RegionGroupVO();
                    vo.setName(entry.getKey());
                    vo.setProvinces(entry.getValue());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void incrementViewCount(Long id) {
        baseMapper.incrementViewCount(id);
    }

    @Override
    public List<DestinationVO> getCitiesByProvince(String province) {
        List<DestinationCity> cities = baseMapper.selectByProvince(province);
        return cities.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<DestinationVO> searchDestinations(String keyword, int limit) {
        LambdaQueryWrapper<DestinationCity> wrapper = Wrappers.lambdaQuery();
        wrapper.like(DestinationCity::getName, keyword)
                .or().like(DestinationCity::getSubtitle, keyword)
                .orderByDesc(DestinationCity::getHotScore)
                .last("LIMIT " + limit);
        
        return this.list(wrapper).stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public void refreshSpotsCount(Long cityId) {
        baseMapper.refreshSpotsCount(cityId);
    }

    @Override
    public void refreshAllSpotsCount() {
        List<DestinationCity> cities = this.list();
        for (DestinationCity city : cities) {
            baseMapper.refreshSpotsCount(city.getId());
        }
    }

    /**
     * 实体转VO
     */
    private DestinationVO convertToVO(DestinationCity city) {
        DestinationVO vo = new DestinationVO();
        vo.setId(city.getId());
        vo.setName(city.getName());
        vo.setSubtitle(city.getSubtitle());
        vo.setImage(city.getCoverUrl());
        vo.setGradient(GRADIENTS.get((int) (city.getId() % GRADIENTS.size())));
        vo.setSpots(city.getSpotsCount());
        vo.setRating(city.getAvgRating());
        vo.setProvince(city.getProvince());
        vo.setIntro(city.getIntro());
        
        // 设置标签
        if (city.getIsHot() == 1) {
            vo.setTag("热门");
        }
        
        return vo;
    }
}
