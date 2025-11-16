package com.sky.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sky.service.OrderService;
import com.sky.mapper.OrderMapper;
import com.sky.vo.OrderSubmitVO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.AddressBookMapper;
import com.sky.exception.AddressBookBusinessException;
import com.sky.constant.MessageConstant;
import com.sky.entity.AddressBook;
import com.sky.context.BaseContext;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import java.util.List;
import org.springframework.beans.BeanUtils;
import java.time.LocalDateTime;
import com.sky.entity.Orders;
import com.sky.entity.OrderDetail;
import java.util.ArrayList;
@Service
public class OderServiceImpl implements OrderService{
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;

    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        
        //业务异常问题(地址为空,购物车为空)
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList == null || shoppingCartList.isEmpty()) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //2.向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setPhone(addressBook.getPhone());
        orders.setConsignee(addressBook.getConsignee());
        orders.setUserId(userId);
        orderMapper.insert(orders);
       
        //3.向订单明细表插入多条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart Cart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(Cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);
        //4.清空当前用户的购物车数据
        shoppingCartMapper.deleteByUserId(userId);
        //5.封装返回VO结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
        .id(orders.getId())
        .orderNumber(orders.getNumber())
        .orderAmount(orders.getAmount())
        .orderTime(orders.getOrderTime())
        .build();
        return orderSubmitVO;
    }
}
