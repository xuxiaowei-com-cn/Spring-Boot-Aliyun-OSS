package cn.com.xuxiaowei.aliyunoss.servlet.aliyun;

import cn.com.xuxiaowei.aliyunoss.setting.AliyunOssSettings;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 阿里云应用服务器（阿里云签名服务器）
 *
 * @author xuxiaowei
 */
@Slf4j
@WebServlet(asyncSupported = true)
public class SignatureServerHttpServlet extends HttpServlet {

    private static final long serialVersionUID = 5522372203700422672L;

    @Autowired
    private AliyunOssSettings aliyunOssSettings;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 请填写您的AccessKeyId。
        String accessId = aliyunOssSettings.getAccessKeyId();
        // 请填写您的AccessKeySecret。
        String accessKey = aliyunOssSettings.getAccessKeySecret();
        // 请填写您的 endpoint。
        String endpoint = aliyunOssSettings.getEndpoint();
        // 请填写您的 bucketname 。
        String bucket = aliyunOssSettings.getBucket();
        // host的格式为 bucketname.endpoint
        String host = "https://" + bucket + "." + endpoint;
        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        String callbackUrl = aliyunOssSettings.getCallbackUrl();
        // 用户上传文件时指定的前缀。
        String dir = aliyunOssSettings.getDir() + LocalDate.now().toString() + "/";

        OSS client = new OSSClientBuilder().build(endpoint, accessId, accessKey);

        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = client.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = client.calculatePostSignature(postPolicy);

            Map<String, String> respMap = new LinkedHashMap<>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

            Map<String, String> jasonCallback = new HashMap<>(8);

            jasonCallback.put("callbackUrl", callbackUrl);
            jasonCallback.put("callbackBody",
                    "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");

            ObjectMapper objectMapper = new ObjectMapper();
            String jasonCallbackStr = objectMapper.writeValueAsString(jasonCallback);

            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallbackStr.getBytes());
            respMap.put("callback", base64CallbackBody);

            String ja1 = objectMapper.writeValueAsString(respMap);

            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "GET, POST");
            response(req, resp, ja1);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     *
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String ossCallbackBody = getPostBody(req.getInputStream(),
                Integer.parseInt(req.getHeader("content-length")));
        boolean ret = verifyOssCallbackRequest(req, ossCallbackBody);
        System.out.println("verify result : " + ret);

        if (ret) {
            response(req, resp, "{\"Status\":\"OK\"}", HttpServletResponse.SC_OK);
        } else {
            response(req, resp, "{\"Status\":\"verdify not ok\"}", HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    /**
     * 获取public key
     */
    private String executeGet(String url) {
        BufferedReader in = null;

        String content = null;

        try {
            // 定义HttpClient
            HttpClient httpClient = HttpClientBuilder.create().build();
            // 实例化HTTP方法
            HttpGet request = new HttpGet();
            request.setURI(new URI(url));

            HttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();

            InputStream inputStream = entity.getContent();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

            in = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();

            String line;

            String nl = System.getProperty("line.separator");

            while ((line = in.readLine()) != null) {
                sb.append(line).append(nl);
            }
            in.close();
            content = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (in != null) {
            try {
                // 最后要关闭BufferedReader
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return content;
    }

    /**
     * 获取Post消息体
     */
    private String getPostBody(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;
            int readLengthThisTime;
            byte[] message = new byte[contentLen];
            try {
                while (readLen != contentLen) {
                    readLengthThisTime = is.read(message, readLen, contentLen - readLen);
                    //不应该发生。
                    if (readLengthThisTime == -1) {
                        break;
                    }
                    readLen += readLengthThisTime;
                }
                return new String(message);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
        return "";
    }

    /**
     * 验证上传回调的Request
     */
    private boolean verifyOssCallbackRequest(HttpServletRequest request, String ossCallbackBody)
            throws NumberFormatException, IOException {
        boolean ret;
        String autorizationInput = request.getHeader("Authorization");
        String pubKeyInput = request.getHeader("x-oss-pub-key-url");
        byte[] authorization = BinaryUtil.fromBase64String(autorizationInput);
        byte[] pubKey = BinaryUtil.fromBase64String(pubKeyInput);
        String pubKeyAddr = new String(pubKey);

        String http = "http://gosspublic.alicdn.com/";
        String https = "http://gosspublic.alicdn.com/";

        if (!pubKeyAddr.startsWith(http)
                && !pubKeyAddr.startsWith(https)) {
            System.out.println("pub key addr必须是oss地址");
            return false;
        }
        String retString = executeGet(pubKeyAddr);
        retString = retString.replace("-----BEGIN PUBLIC KEY-----", "");
        retString = retString.replace("-----END PUBLIC KEY-----", "");
        String queryString = request.getQueryString();
        String uri = request.getRequestURI();
        String authStr = java.net.URLDecoder.decode(uri, "UTF-8");
        if (queryString != null && !"".equals(queryString)) {
            authStr += "?" + queryString;
        }
        authStr += "\n" + ossCallbackBody;
        ret = doCheck(authStr, authorization, retString);
        return ret;
    }

    /**
     * 验证RSA
     */
    private static boolean doCheck(String content, byte[] sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = BinaryUtil.fromBase64String(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature.getInstance("MD5withRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes());
            return signature.verify(sign);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 服务器响应结果
     */
    private void response(HttpServletRequest request, HttpServletResponse response, String results, int status)
            throws IOException {
        String callbackFunName = request.getParameter("callback");
        response.addHeader("Content-Length", String.valueOf(results.length()));
        if (callbackFunName == null || "".equalsIgnoreCase(callbackFunName)) {
            response.getWriter().println(results);
        } else {
            response.getWriter().println(callbackFunName + "( " + results + " )");
        }
        response.setStatus(status);
        response.flushBuffer();
    }

    /**
     * 服务器响应结果
     */
    private void response(HttpServletRequest request, HttpServletResponse response, String results) throws IOException {
        String callbackFunName = request.getParameter("callback");
        if (callbackFunName == null || "".equalsIgnoreCase(callbackFunName)) {
            response.getWriter().println(results);
        } else {
            response.getWriter().println(callbackFunName + "( " + results + " )");
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.flushBuffer();
    }

}
