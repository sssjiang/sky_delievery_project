package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单明细数据
     * @param orderDetailList
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id查询订单明细
     * @param orderId
     * @return
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);

    /**
     * 根据订单id删除订单明细
     * @param orderId
     */
    @Delete("delete from order_detail where order_id = #{orderId}")
    void deleteByOrderId(Long orderId);

    /**
     * 根据订单号查询订单明细
     * @param orderNumber
     * @return
     */
    @Select("select od.* from order_detail od, orders o where od.order_id = o.id and o.number = #{orderNumber}")
    List<OrderDetail> getByOrderNumber(String orderNumber);

    /**
     * 根据订单明细id查询
     * @param id
     * @return
     */
    @Select("select * from order_detail where id = #{id}")
    OrderDetail getById(Long id);

    /**
     * 根据菜品id统计订单明细数量
     * @param dishId
     * @return
     */
    @Select("select count(*) from order_detail where dish_id = #{dishId}")
    Integer countByDishId(Long dishId);

    /**
     * 根据套餐id统计订单明细数量
     * @param setmealId
     * @return
     */
    @Select("select count(*) from order_detail where setmeal_id = #{setmealId}")
    Integer countBySetmealId(Long setmealId);
}


