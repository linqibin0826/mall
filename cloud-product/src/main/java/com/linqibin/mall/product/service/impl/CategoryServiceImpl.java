package com.linqibin.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.linqibin.mall.product.service.CategoryBrandRelationService;
import com.linqibin.mall.product.service.CategoryService;
import com.linqibin.mall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
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

import javax.annotation.Resource;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

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


    /**
     * 驱逐category相关的所有缓存
     */
    @CacheEvict(value = "category", allEntries = true) // 失效模式
    // @CachePut 双写模式，会把返回值放到redis中。
    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCatalogName(category.getCatId(), category.getName());
    }

    // 过期时间需要在配置文件中指定，将数据保存为json数据
    @Cacheable(value = {"category"}, key = "#root.methodName")
    @Override
    public List<CategoryEntity> queryLevelOneCategories() {
        return this.list(new QueryWrapper<CategoryEntity>().lambda().eq(CategoryEntity::getParentCid, "0"));
    }

    /**
     * 默认没有加锁 sync=false
     */
    @Cacheable(value = {"category"}, key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        System.out.println("查询了数据库......");
        List<CategoryEntity> all = list();
        Map<String, List<Catelog2Vo>> resultMap = all.parallelStream().filter(item -> item.getParentCid() == 0)
                .collect(Collectors.toMap(k -> k.getCatId().toString(), v -> Lists.newArrayList()));
        Map<String, List<CategoryEntity>> groupByParentId = all.stream()
                .collect(Collectors.groupingBy(categoryEntity -> categoryEntity.getParentCid().toString()));
        // 根据所有1级分类id 查找所有的2级分类和3级分类vo
        resultMap.forEach((level1Id, value) -> {
            List<Catelog2Vo> catelog2Vos = groupByParentId.get(level1Id).stream().map(item -> {
                // 查找2级分类下面的子分类
                String level2Id = item.getCatId().toString();
                List<Catelog2Vo.Catalog3Vo> catalog3Vos = groupByParentId.get(level2Id).stream()
                        .map(subItem -> new Catelog2Vo.Catalog3Vo(subItem.getCatId().toString(), subItem.getName()))
                        .collect(Collectors.toList());
                return new Catelog2Vo(level2Id, item.getName(), level1Id, catalog3Vos);
            }).collect(Collectors.toList());
            // 修改当前查找出来的数据
            resultMap.put(level1Id, catelog2Vos);
        });
        return resultMap;
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
