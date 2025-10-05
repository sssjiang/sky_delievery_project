package com.sky.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;
import com.sky.result.Result;
import com.sky.dto.SetmealDTO;
import org.springframework.beans.factory.annotation.Autowired;
import com.sky.service.SetmealService;
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @PostMapping
    @ApiOperation("新增套餐")
    public Result<String> save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐:{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }
}
