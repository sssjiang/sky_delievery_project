package com.sky.controller.user;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import com.sky.result.Result;
import com.sky.dto.UserLoginDTO;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.sky.service.UserService;
import com.sky.entity.User;
import com.sky.utils.JwtUtil;
import com.sky.properties.JwtProperties;
import com.sky.constant.JwtClaimsConstant;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags = "C端用户相关接口")
public class UserController {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信用户登录: {}", userLoginDTO.getCode());
        User user = userService.wxlogin(userLoginDTO);
        //生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVO userLoginVO = UserLoginVO.builder()
            .id(user.getId())
            .openid(user.getOpenid())
            .token(token)
            .build();
        return Result.success(userLoginVO);
    }
}
