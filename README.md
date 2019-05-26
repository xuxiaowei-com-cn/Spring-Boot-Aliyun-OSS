# 欢迎捐助

- 如果您觉得本项目对您有帮助，请捐助，谢谢。

- 您的捐助就是我最大的动力。

<p align=center>
  <a href="https://xuxiaowei.com.cn">
    <img src="https://cdn2.xuxiaowei.com.cn/img/QRCode.png/xuxiaowei.com.cn" alt="徐晓伟工作室" width="360">
  </a>
</p>


# Spring-Boot-Aliyun-OSS
Spring Boot 阿里云 OSS。

- 本项目是根据 [服务端签名直传并设置上传回调](https://help.aliyun.com/document_detail/31927.html)(更新时间：2019-04-08 16:20:36)修正的

- 官方源码：

    - [服务端签名直传并设置上传回调 Java 版](https://help.aliyun.com/document_detail/91868.html)(更新时间：2019-04-09 16:47:12)
    
    - 注：源码中有错误

# 使用说明

## 修改 application.yml

- access-key-id

    - 云账号：https://usercenter.console.aliyun.com/，
        提示信息云账号AccessKey是您访问阿里云API的密钥，具有该账户完全的权限，请您务必妥善保管！
        不要通过任何方式(eg, Github)将AccessKey公开到外部渠道，以避免被他人利用而造成 [安全威胁](https://help.aliyun.com/knowledge_detail/54059.html) 。
        强烈建议您遵循 [阿里云安全最佳实践](https://help.aliyun.com/document_detail/28642.html) ，使用RAM子用户AccessKey来进行API调用。
    
    - 子用户：https://ram.console.aliyun.com/overview
    
        - 子用户需要的权限：AliyunOSSFullAccess（管理对象存储服务(OSS)权限）

- access-key-secret

    - 上面 access-key-id 对应的 access-key-secret

- endpoint
    
    - [OSS管理平台](https://oss.console.aliyun.com/overview)
    
    - 如：oss-cn-qingdao.aliyuncs.com（概览 --> 访问域名 --> EndPoint（地域节点） --> 外网访问）

- bucket

    - 如：xuxiaowei-test-oss（概览 --> 访问域名 --> Bucket 域名 --> 外网访问（不包括 EndPoint））

- dir

    - 上传地址前缀，可根据上传参数等自定义

- callback-url

    - 回调地址，暂无效

- static/upload.js

    - serverUrl
    
        - 修正为：cn.com.xuxiaowei.aliyunoss.servlet.aliyun.SignatureServerHttpServlet 的 URL

# 依赖

## Spring Boot 依赖（创建项目时选择）

- Spring Boot
    - 2.1.5.RELEASE

- Core
    - Lombok                    注解（Getter/Setter）。
    - Configuration Processor   为您的自定义配置键生成元数据（注解处理器）。
    
- Web
    - Web                       使用Tomcat和Spring MVC进行全栈Web开发
    
- Template
	- Thymeleaf                 Thymeleaf模板引擎（页面）
    
## 其他依赖（创建项目时不可选）

- 阿里云 OSS

    - aliyun-sdk-oss            阿里云OSS
    
    - json-lib                  阿里云OSS所需依赖，Maven库中不存在，请使用 sources 文件夹中的 json-lib-2.4-jdk15.jar
    
    - commons-beanutils         阿里云OSS所需依赖
    
    - commons-collections       阿里云OSS所需依赖
    
    - ezmorph                   阿里云OSS所需依赖
