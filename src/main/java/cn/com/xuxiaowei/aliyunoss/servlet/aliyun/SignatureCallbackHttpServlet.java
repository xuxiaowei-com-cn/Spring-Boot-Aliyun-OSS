package cn.com.xuxiaowei.aliyunoss.servlet.aliyun;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 阿里云回调服务器
 *
 * @author xuxiaowei
 */
public class SignatureCallbackHttpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.err.println("回调服务器GET");

        Map<String, String[]> parameterMap = req.getParameterMap();
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        for (Map.Entry<String, String[]> entrie : entries) {
            System.out.println(entrie.getKey() + "\t" + Arrays.toString(entrie.getValue()));
        }

        resp.getWriter().println("{\"Status\":\"OK\",\"method\":\"GET\"}");
        resp.flushBuffer();

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        System.err.println("回调服务器POST");

        Map<String, String[]> parameterMap = req.getParameterMap();
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        for (Map.Entry<String, String[]> entrie : entries) {
            System.out.println(entrie.getKey() + "\t" + Arrays.toString(entrie.getValue()));
        }

        resp.getWriter().println("{\"Status\":\"OK\",\"method\":\"POST\"}");
        resp.flushBuffer();

    }
}
