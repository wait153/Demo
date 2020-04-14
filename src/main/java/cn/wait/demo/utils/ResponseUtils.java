package cn.wait.demo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @description
 * @date 2020/4/10
 */
public class ResponseUtils {
    /**
     * 发送HTTP响应信息,JSON格式
     *
     * @param response HTTP响应对象
     * @param message 输出对象
     * @throws IOException 抛出异常，由调用者捕获处理
     */
    public static void write(HttpServletResponse response, Object message,int status) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        ObjectMapper mapper = new ObjectMapper();
        try (
                PrintWriter writer = response.getWriter();
        ) {
            writer.write(mapper.writeValueAsString(message));
            writer.flush();
        }
    }
}
