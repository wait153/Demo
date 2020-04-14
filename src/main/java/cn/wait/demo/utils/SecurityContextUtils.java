package cn.wait.demo.utils;

import cn.wait.demo.entity.JwtUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @description
 * @date 2020/4/10
 */
public class SecurityContextUtils {
    public static JwtUser getJwtUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if(principal instanceof JwtUser){
            return  (JwtUser)principal;
        }
        return null;
    }

}
