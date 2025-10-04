package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> dishFlavors);
    // 根据菜品id删除口味
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    void deleteByDishIds(List<Long> dishIds);
}
