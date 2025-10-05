package com.sky.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
@Mapper

public interface SetmealDishMapper {
    // 根据菜品id查询套餐id 
    // select setmeal_id from setmeal_dish where dish_id in (1,2,3)
    //动态sql写在xml文件中
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
    void insertBatch(List<SetmealDish> setmealDishes);
    /**
     * 根据套餐id查询套餐菜品关系
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);
    /**
     * 根据套餐id删除套餐菜品关系
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}
