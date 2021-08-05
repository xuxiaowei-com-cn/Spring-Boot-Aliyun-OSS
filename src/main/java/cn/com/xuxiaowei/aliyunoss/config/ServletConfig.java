package cn.com.xuxiaowei.aliyunoss.config;

import cn.com.xuxiaowei.aliyunoss.servlet.aliyun.SignatureCallbackHttpServlet;
import cn.com.xuxiaowei.aliyunoss.servlet.aliyun.SignatureServerHttpServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServlet;

/**
 * Servlet 配置
 *
 * @author xuxiaowei
 */
@Configuration
public class ServletConfig {

    /**
     * 阿里云应用服务器（阿里云签名服务器） 注册为 Bean
     */
    @Bean
    public SignatureServerHttpServlet aliyunOssSignatureServerHttpServletBean() {
        return new SignatureServerHttpServlet();
    }

    /**
     * 阿里云应用服务器（阿里云签名服务器）
     */
    @Bean
    public ServletRegistrationBean<HttpServlet> aliyunOssSignatureServerHttpServlet() {
        return new ServletRegistrationBean<>(aliyunOssSignatureServerHttpServletBean(), "/aliyun/oss/signature");
    }

    /**
     * 阿里云回调服务器
     */
    @Bean
    public ServletRegistrationBean<HttpServlet> aliyunOssSignatureCallbackHttpServlet() {
        return new ServletRegistrationBean<>(new SignatureCallbackHttpServlet(), "/aliyun/oss/callback");
    }

}
