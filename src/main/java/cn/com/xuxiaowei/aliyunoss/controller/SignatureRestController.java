package cn.com.xuxiaowei.aliyunoss.controller;

import cn.com.xuxiaowei.aliyunoss.setting.AliyunOssSettings;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.Date;

/**
 * 签名 {@link RestController}
 *
 * @author 徐晓伟
 * @see <a href="https://help.aliyun.com/document_detail/32016.html">授权访问</a>
 */
@RestController
@RequestMapping("/signature")
public class SignatureRestController {

    private AliyunOssSettings aliyunOssSettings;

    @Autowired
    public void setAliyunOssSettings(AliyunOssSettings aliyunOssSettings) {
        this.aliyunOssSettings = aliyunOssSettings;
    }

    /**
     * 根据 objectName 生成一定时间内可访问的 URL
     * <p>
     * 到期后不可访问
     *
     * @param request    请求
     * @param response   响应
     * @param objectName 填写Object完整路径，例如exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * @return 返回 一定时间内可访问的 URL
     */
    @RequestMapping("/url")
    public String url(HttpServletRequest request, HttpServletResponse response, String objectName) {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = aliyunOssSettings.getEndpoint();
        // 从STS服务获取的临时访问密钥（AccessKey ID和AccessKey Secret）。
        String accessKeyId = aliyunOssSettings.getAccessKeyId();
        String accessKeySecret = aliyunOssSettings.getAccessKeySecret();
        // 从STS服务获取的安全令牌（SecurityToken）。
        // String securityToken = "yourSecurityToken";
        // 填写Bucket名称，例如examplebucket。
        String bucketName = aliyunOssSettings.getBucket();

        // 创建OSSClient实例。
        // OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret, securityToken);
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 设置签名URL过期时间为3600秒（1小时）。
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        URL url = ossClient.generatePresignedUrl(bucketName, objectName, expiration);
        System.out.println(url);
        // 关闭OSSClient。
        ossClient.shutdown();

        return url.toString();
    }

}
