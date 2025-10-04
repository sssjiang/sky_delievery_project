package com.sky.service.impl;

import org.springframework.stereotype.Service;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.dto.DishDTO;
import org.springframework.transaction.annotation.Transactional;
import com.sky.entity.Dish;
import org.springframework.beans.BeanUtils;
import java.util.List;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.vo.DishVO;
import com.sky.constant.StatusConstant;
import com.sky.constant.MessageConstant;
import com.sky.mapper.SetmealDishMapper;
@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
      //1.向菜品表插入一条数据
      Dish dish = new Dish();
      BeanUtils.copyProperties(dishDTO, dish);
      dishMapper.insert(dish);

      Long dishId = dish.getId();
      //2.向口味表插入多条数据
      List<DishFlavor> dishFlavors = dishDTO.getFlavors();
      if (dishFlavors != null && dishFlavors.size() > 0) {
        dishFlavors.forEach(dishFlavor -> {
          dishFlavor.setDishId(dishId);
        });
        dishFlavorMapper.insertBatch(dishFlavors);
      }
    }
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
      PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
      Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
      return new PageResult(page.getTotal(), page.getResult());
    }
    // 事物注解，保持原子性
    @Transactional
    public void deleteBatch(List<Long> ids) {
      //1.判断当前的菜品是否可以删除---是否存在起售中的菜品？
      ids.forEach(id -> {
        Dish dish = dishMapper.getById(id);
        if (dish.getStatus() == StatusConstant.ENABLE) {
          throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
      });

      //2.判断当前的菜品是否被套餐关联
      List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
      if (setmealIds != null && setmealIds.size() > 0) {
        throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
      }
      //3.删除菜品表中的菜品
      // for (Long id : ids) {
      //   dishMapper.deleteById(id);
      //    //删除菜品关联的口味数据
      //   dishFlavorMapper.deleteByDishId(id);
      // }
      //sql:delete from dish where id in (1,2,3)
      //根据菜品ids批量删除菜品 优化写法
      dishMapper.deleteByIds(ids);
      //根据菜品ids批量删除口味 优化写法
      dishFlavorMapper.deleteByDishIds(ids);

    }
       /**
     * 根据id查询菜品和口味
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {
      // 根据id查询菜品
      Dish dish = dishMapper.getById(id);
      // 根据菜品id查询口味
      List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
      // 将菜品和口味封装到DishVO中
      DishVO dishVO = new DishVO();
      BeanUtils.copyProperties(dish, dishVO);
      dishVO.setFlavors(dishFlavors);
      return dishVO;
    
    
    }
}
