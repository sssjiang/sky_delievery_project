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
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.vo.DishVO;
@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
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
}
