package com.sky.service.impl;

import org.springframework.stereotype.Service;
import com.sky.service.UserService;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import com.sky.utils.HttpClientUtil;
import com.sky.properties.WeChatProperties;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.sky.constant.MessageConstant;
import com.sky.mapper.UserMapper;
import java.time.LocalDateTime;
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    //微信服务接口地址
    @Autowired
    private WeChatProperties wechatProperties;
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private UserMapper userMapper;
    @Override
    public User wxlogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获取微信用户信息
        String openid = getOpenid(userLoginDTO.getCode());
        //判断Openid是否为空，如果为空登陆失败，则抛出异常
        if (openid == null) {
            throw new RuntimeException(MessageConstant.LOGIN_FAILED);
        }
        //判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            //如果是新用户自动完成注册
            user = User.builder()
                .openid(openid)
                .createTime(LocalDateTime.now())
                .build();
            userMapper.insert(user);
        }

        //返回用户信息
        return user;
    }
    private String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", wechatProperties.getAppid());
        map.put("secret", wechatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
