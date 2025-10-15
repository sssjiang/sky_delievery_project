package com.sky.service;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import java.util.List;
import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;
public interface SetmealService {
    public void saveWithDish(SetmealDTO setmealDTO);
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    SetmealVO getById(Long id);
    void update(SetmealDTO setmealDTO);
    
    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);
    /**
     * 套餐起售、停售
     * @param status
     * @param id
    */
    void startOrStop(Integer status, Long id);
     /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);
     /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
	
}
