package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.sky.annotation.AutoFill;
import com.sky.enumeration.OperationType;
import com.sky.entity.Dish;
import com.sky.dto.DishPageQueryDTO;
import com.sky.vo.DishVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);
    // 根据主键id删除菜品
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);
    // 根据菜品ids批量删除菜品
    void deleteByIds(List<Long> ids);
    // 更新菜品
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);
    
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
   /**
     * 动态条件查询菜品
     * @param dish
     * @return
    */
    List<Dish> list(Dish dish);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
    */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);
    
    /**
     * 启停售菜品
     * @param id
     * @param status
     */
    void updateStatusById(Long id, Integer status);
}
