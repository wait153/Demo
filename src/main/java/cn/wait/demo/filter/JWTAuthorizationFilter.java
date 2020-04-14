package cn.wait.demo.filter;

import cn.wait.demo.consts.TokenConst;
import cn.wait.demo.entity.JwtUser;
import cn.wait.demo.exception.TokenException;
import cn.wait.demo.utils.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {


    private RedisTemplate<String, Object> redisTemplate;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, RedisTemplate<String, Object> rTemplate) {
        super(authenticationManager);
        redisTemplate = rTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String tokenHeader = request.getHeader(JwtTokenUtils.TOKEN_HEADER);
        try {
            // 如果请求头中没有Authorization信息则直接放行了
            if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
//            chain.doFilter(request, response);
                throw new TokenException("Token错误");
            }
            // 如果请求头中有token，则进行解析，并且设置认证信息

            String token = tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX, "");
            boolean expiration = JwtTokenUtils.isExpiration(token);
            JwtUser user = JwtTokenUtils.getUserFromToken(token);
            if (null == user || StringUtils.isEmpty(String.valueOf(redisTemplate.opsForValue().get(TokenConst.TOKEN_PREFIX + user.getId()))) || expiration) {
                throw new TokenException("请您重新登录");
            }

        } catch (Exception e) {
            //返回json形式的错误信息
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            String reason = "统一处理，原因：" + e.getMessage();
            response.getWriter().write(new ObjectMapper().writeValueAsString(reason));
            response.getWriter().flush();
            return;
        }
        super.doFilterInternal(request, response, chain);
    }
}
