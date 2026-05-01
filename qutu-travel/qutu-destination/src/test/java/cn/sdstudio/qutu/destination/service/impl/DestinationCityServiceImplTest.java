package cn.sdstudio.qutu.destination.service.impl;

import cn.sdstudio.qutu.destination.mapper.DestinationCityMapper;
import cn.sdstudio.qutu.destination.vo.RegionGroupVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DestinationCityServiceImplTest {

    private DestinationCityServiceImpl service;
    private DestinationCityMapper mockMapper;

    @BeforeEach
    void setUp() throws Exception {
        service = new DestinationCityServiceImpl();
        mockMapper = mock(DestinationCityMapper.class);

        // 将 protected 字段 baseMapper 注入为 mock
        Field baseMapperField = service.getClass().getSuperclass().getDeclaredField("baseMapper");
        baseMapperField.setAccessible(true);
        baseMapperField.set(service, mockMapper);
    }

    @Test
    @DisplayName("getHotCities 应返回 mapper 提供的数据")
    void testGetHotCities() {
        when(mockMapper.selectHotCities(3)).thenReturn(Arrays.asList("北京", "上海", "广州"));

        List<String> result = service.getHotCities(3);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("北京"));
        verify(mockMapper, times(1)).selectHotCities(3);
    }

    @Test
    @DisplayName("getRegionGroups 应包含 北方地区 分组")
    void testGetRegionGroups() {
        List<RegionGroupVO> groups = service.getRegionGroups();
        assertNotNull(groups);
        assertTrue(groups.stream().anyMatch(g -> "北方地区".equals(g.getName())));
    }
}
