package com.atguigu.gulimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;

import static com.sun.javafx.robot.impl.FXRobotHelper.getChildren;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override // service层
    public List<CategoryEntity> listWithTree() {
        // 怎么拿categoryDao？
    /*
        * 继承了ServiceImpl<CategoryDao, CategoryEntity>
        有个属性baseMapper，自动注入
        * */

        // 1 查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        // 2 组装成父子的树型结构
        // 2.1 找到所有一级分类
        List<CategoryEntity> level1Menus = categoryEntities.stream().filter(
                // 找到一级
                categoryEntity -> categoryEntity.getParentCid() == 0
        ).map(menu->{
            // 把当前的child属性改了之后重新返回
            menu.setChildren(getChildren(menu,categoryEntities));
            return menu;
        }).sorted((menu1,menu2)->
                menu1.getSort()-menu2.getSort()).collect(Collectors.toList());

        return level1Menus;
        //        return categoryEntities;
    }

    @Override // CategoryServiceImpl
    public Long[] findCateLogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        paths = findParentPath(catelogId, paths);
        // 收集的时候是顺序 前端是逆序显示的 所以用集合工具类给它逆序一下
        // 子父 转 父子
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]); // 1级  2级  3级
    }
    /**
     * 递归收集所有父分类
     */
    private List<Long> findParentPath(Long catlogId, List<Long> paths) {
        // 1、收集当前节点id
        paths.add(catlogId);// 比如父子孙层级，返回的是 孙 子 父
        CategoryEntity parent_Id = this.getById(catlogId);
        if (parent_Id.getParentCid() != 0) {
            // 递归
            findParentPath(parent_Id.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 获取子菜单
     * @param currentMenu 当前菜单
     * @param allMenu 所有菜单
     * @return 所有子菜单
     */
    private List<CategoryEntity> getChildren(CategoryEntity currentMenu,List<CategoryEntity> allMenu){
        List<CategoryEntity> childrents = allMenu.stream().filter(categoryEntity -> categoryEntity.getParentCid() == currentMenu.getCatId())
                // 每个子菜单可能还有子菜单
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildren(categoryEntity, allMenu));
                    return categoryEntity;
                })
                // 排序
                .sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort())))
                .collect(Collectors.toList());;
        return childrents;

    }

}