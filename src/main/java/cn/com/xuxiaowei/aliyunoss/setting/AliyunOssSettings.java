package cn.com.xuxiaowei.aliyunoss.setting;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 OSS 配置
 *
 * @author xuxiaowei
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOssSettings {

    private String accessKeyId;
    private String accessKeySecret;

    /**
     * EndPoint（地域节点）
     */
    private String endpoint;

    /**
     * Bucket 域名（不含EndPoint（地域节点））
     */
    private String bucket;

    /**
     * 上传地址前缀
     */
    private String dir;

    /**
     * 回调服务器地址
     */
    private String callbackUrl;

}
