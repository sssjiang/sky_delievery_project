package com.sky.service;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
public interface SetmealService {
    public void saveWithDish(SetmealDTO setmealDTO);
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
}
