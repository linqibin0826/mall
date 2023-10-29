package com.linqibin.mall.product.service.impl;

import com.linqibin.mall.product.service.CategoryBrandRelationService;
import com.linqibin.mall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linqibin.common.utils.PageUtils;
import com.linqibin.common.utils.Query;

import com.linqibin.mall.product.dao.CategoryDao;
import com.linqibin.mall.product.entity.CategoryEntity;
import org.springframework.transaction.annotation.Transactional;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1.查询所有分类
        List<CategoryEntity> allCategory = this.list(null);

        // 2.组装成父子类型的树形结构
        //   1).找到所有的一级分类
        return allCategory.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .peek(levelOneCategory -> {
                    // 2).找到子分类等
                    levelOneCategory.setChildren(getChildCategories(levelOneCategory, allCategory));
                })
                .sorted(Comparator.comparingInt(category -> (category.getSort() == null ? 0 : category.getSort())))
                .collect(Collectors.toList());
    }

    @Override
    public void removeCategoriesByIds(List<Long> asList) {
        // TODO 检查当前删除的分类,是否被其他地方引用
        baseMapper.deleteBatchIds(asList);
    }


    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCatalogName(category.getCatId(), category.getName());
    }

    /**
     * 递归查找所有的分类与其子分类
     *
     * @author hugh&you
     * @since 2021/1/13 20:32
     */
    private List<CategoryEntity> getChildCategories(CategoryEntity parentCategory, List<CategoryEntity> allCategory) {
        return allCategory.stream()
                // 根据父ID过滤,查找出所有二级分类
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(parentCategory.getCatId()))
                .peek(categoryEntity -> {
                    // 1.调用递归, 查找出所有三级分类, 以此类推.
                    categoryEntity.setChildren(getChildCategories(categoryEntity, allCategory));
                })
                // 排序
                .sorted(Comparator.comparingInt(category -> (category.getSort() == null ? 0 : category.getSort())))
                .collect(Collectors.toList());
    }
}
