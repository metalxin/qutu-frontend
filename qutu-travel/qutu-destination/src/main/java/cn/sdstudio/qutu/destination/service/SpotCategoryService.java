package cn.sdstudio.qutu.destination.service;

import cn.sdstudio.qutu.destination.entity.SpotCategory;
import cn.sdstudio.qutu.destination.vo.CategoryVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 景点分类Service
 *
 * @author qutu
 * @date 2026-02-02
 */
public interface SpotCategoryService extends IService<SpotCategory> {

    /**
     * 获取分类树
     */
    List<CategoryVO> getCategoryTree();

    /**
     * 获取子分类
     */
    List<CategoryVO> getSubCategories(Long parentId);

    /**
     * 获取顶级分类
     */
    List<CategoryVO> getRootCategories();
}
