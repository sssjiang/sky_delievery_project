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
import java.util.Arrays;
import java.util.ArrayList;
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
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
      Dish dish = new Dish();
      BeanUtils.copyProperties(dishDTO, dish);
      // 更新菜品
      dishMapper.update(dish);
      // 删除口味
      dishFlavorMapper.deleteByDishId(dishDTO.getId());
      // 插入口味
      List<DishFlavor> dishFlavors = dishDTO.getFlavors();
      if (dishFlavors != null && dishFlavors.size() > 0) {
        dishFlavors.forEach(dishFlavor -> {
          dishFlavor.setDishId(dishDTO.getId());
        });
        dishFlavorMapper.insertBatch(dishFlavors);
      }
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
*/
public List<Dish> list(Long categoryId) {
  Dish dish = Dish.builder()
      .categoryId(categoryId)
      .status(StatusConstant.ENABLE)
      .build();
  return dishMapper.list(dish);
}

/**
 * 启停售菜品
 * @param id
 * @param status
 */
@Transactional
public void updateStatusById(Long id, Integer status) {
    // 如果是停售操作，需要检查菜品是否被套餐关联,关联套餐如果是启售状态，则提示"套餐内包含启售菜品，无法停售"
    if (status == StatusConstant.DISABLE) {
      List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(Arrays.asList(id));
      if (setmealIds != null && setmealIds.size() > 0) {
        throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
      }
    }
    
    // 更新菜品状态
    dishMapper.updateStatusById(id, status);
}

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
      List<Dish> dishList = dishMapper.list(dish);

      List<DishVO> dishVOList = new ArrayList<>();

      for (Dish d : dishList) {
          DishVO dishVO = new DishVO();
          BeanUtils.copyProperties(d,dishVO);

          //根据菜品id查询对应的口味
          List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

          dishVO.setFlavors(flavors);
          dishVOList.add(dishVO);
      }

      return dishVOList;
  }
}
