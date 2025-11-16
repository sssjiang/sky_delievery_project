package com.sky.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import com.sky.result.Result;
import com.sky.vo.OrderSubmitVO;
import com.sky.dto.OrdersSubmitDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.annotations.ApiOperation;
import com.sky.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "C端-订单接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
 @PostMapping("/submit")
 @ApiOperation("用户提交订单")
 public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
    log.info("用户提交订单:{}",ordersSubmitDTO);
    OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
    return Result.success(orderSubmitVO);
 }
}
