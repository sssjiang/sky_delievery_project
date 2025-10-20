package com.sky.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import com.sky.service.ShoppingCartService;
import com.sky.dto.ShoppingCartDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.entity.ShoppingCart;
import com.sky.context.BaseContext;
import org.springframework.beans.BeanUtils;
import java.util.List;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import java.time.LocalDateTime;
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //判断当前购物车中是否存在该商品
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);


        //如果存在，则数量加1
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        } else {
            // 判断是菜品还是套餐
           Long dishId = shoppingCartDTO.getDishId();
            if(dishId != null){
                // 菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setName(dish.getName());
            }else{
                // 套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setName(setmeal.getName());
               
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

        //如果不存在，则新增商品

      
    }
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart =ShoppingCart.builder()
        .userId(userId)
        .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }   
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByUserId(userId);
    }
}
