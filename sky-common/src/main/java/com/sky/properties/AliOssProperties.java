package com.sky.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
/*
 * 配置属性类,读取application.yml中的alioss配置,封装成java类对象
 * spring boot可以将"-"的命名方式,转换成驼峰命名方式,例如:access-key-id -> accessKeyId
 */
@ConfigurationProperties(prefix = "sky.alioss")
@Data
public class AliOssProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

}
