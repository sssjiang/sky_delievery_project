package com.sky.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.sky.utils.AliOssUtil;
import java.util.UUID;
import java.io.IOException;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传:{}", file);
        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //获取文件后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //生成新的文件名
            String newFilename = UUID.randomUUID().toString() + extension;
            //上传文件
            String url = aliOssUtil.upload(file.getBytes(), newFilename);
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
