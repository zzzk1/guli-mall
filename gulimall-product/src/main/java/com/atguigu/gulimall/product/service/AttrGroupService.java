package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;

import java.util.Map;

/**
 * 属性分组
 *
 * @author zzzk1
 * @email madzhou1@gmail.com
 * @date 2023-04-06 19:09:35
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

//    PageUtils queryPage(Map<String, Object> params);

    // 根据分类返回属性分组 // AttrGroupServiceImpl.java // 按关键字或者按id查
    PageUtils queryPage(Map<String, Object> params, Long catelogId);
}

