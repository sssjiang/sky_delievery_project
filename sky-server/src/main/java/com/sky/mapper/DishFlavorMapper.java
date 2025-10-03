package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

import com.sky.entity.DishFlavor;

@Mapper
public interface DishFlavorMapper {
    void insertBatch(List<DishFlavor> dishFlavors);
    
}
