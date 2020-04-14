package cn.wait.demo.config;

import cn.wait.demo.utils.ResponseUtils;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @description
 * @date 2020/4/10
 */
public class SessionInformationExpiredStrategyImpl implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {

        ResponseUtils.write(event.getResponse(), "你的账号在另一地点被登录",401);
    }

}