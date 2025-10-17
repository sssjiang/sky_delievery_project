package com.sky.mapper;

import java.util.List;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;
@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);
    @Insert("insert into shopping_cart (user_id, dish_id, setmeal_id,dish_flavor, number, amount, image, name, create_time) values (#{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{image}, #{name}, #{createTime})")
    void insert(ShoppingCart shoppingCart);
}
