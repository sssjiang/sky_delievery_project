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
}
