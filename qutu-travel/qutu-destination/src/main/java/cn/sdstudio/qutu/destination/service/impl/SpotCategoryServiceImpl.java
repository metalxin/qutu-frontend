package cn.sdstudio.qutu.destination.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.sdstudio.qutu.destination.entity.SpotCategory;
import cn.sdstudio.qutu.destination.mapper.SpotCategoryMapper;
import cn.sdstudio.qutu.destination.service.SpotCategoryService;
import cn.sdstudio.qutu.destination.vo.CategoryVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 景点分类Service实现
 *
 * @author qutu
 * @date 2026-02-02
 */
@Service
@RequiredArgsConstructor
public class SpotCategoryServiceImpl extends ServiceImpl<SpotCategoryMapper, SpotCategory> 
        implements SpotCategoryService {

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<SpotCategory> roots = baseMapper.selectRootCategories();
        return roots.stream().map(this::convertToTreeVO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getSubCategories(Long parentId) {
        List<SpotCategory> categories = baseMapper.selectByParentId(parentId);
        return categories.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getRootCategories() {
        List<SpotCategory> roots = baseMapper.selectRootCategories();
        return roots.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    private CategoryVO convertToTreeVO(SpotCategory category) {
        CategoryVO vo = convertToVO(category);
        List<SpotCategory> children = baseMapper.selectByParentId(category.getId());
        if (children == null || children.isEmpty()) {
            vo.setChildren(Collections.emptyList());
            return vo;
        }
        vo.setChildren(children.stream().map(this::convertToTreeVO).collect(Collectors.toList()));
        return vo;
    }

    private CategoryVO convertToVO(SpotCategory category) {
        CategoryVO vo = new CategoryVO();
        BeanUtil.copyProperties(category, vo);
        return vo;
    }
}
