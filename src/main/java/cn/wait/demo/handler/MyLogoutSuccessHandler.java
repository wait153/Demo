package cn.wait.demo.handler;

import cn.wait.demo.consts.TokenConst;
import cn.wait.demo.entity.JwtUser;
import cn.wait.demo.utils.SecurityContextUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description
 * @date 2020/4/10
 */
@Component
public class MyLogoutSuccessHandler implements LogoutSuccessHandler {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication){
        //退出业务逻辑

        try {
//            Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
//            System.out.println(authentication+"++++++"+authentication1);
            Object spring_security_context = request.getSession().getAttribute("spring_security_context");
            if(null !=authentication){
                JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
                redisTemplate.delete(TokenConst.TOKEN_PREFIX + jwtUser.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}