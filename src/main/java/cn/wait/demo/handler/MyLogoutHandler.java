package cn.wait.demo.handler;

import cn.wait.demo.consts.TokenConst;
import cn.wait.demo.entity.JwtUser;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
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
public class MyLogoutHandler implements LogoutHandler {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) {
        try {
            Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
            System.out.println(authentication+"++++++"+authentication1);
            Object spring_security_context = httpServletRequest.getSession().getAttribute("spring_security_context");
            if(null !=authentication){
                JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
                redisTemplate.delete(TokenConst.TOKEN_PREFIX + jwtUser.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}