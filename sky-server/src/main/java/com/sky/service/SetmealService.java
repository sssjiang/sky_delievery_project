package com.sky.service;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
public interface SetmealService {
    public void saveWithDish(SetmealDTO setmealDTO);
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    SetmealVO getById(Long id);
    void update(SetmealDTO setmealDTO);
}
