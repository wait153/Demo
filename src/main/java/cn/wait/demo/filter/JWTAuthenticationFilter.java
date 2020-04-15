package cn.wait.demo.filter;

import cn.wait.demo.consts.TokenConst;
import cn.wait.demo.entity.JwtUser;
import cn.wait.demo.model.LoginUser;
import cn.wait.demo.utils.JwtTokenUtils;
import cn.wait.demo.utils.ResponseUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private ThreadLocal<Integer> rememberMe = new ThreadLocal<>();

    private RedisTemplate<String, Object> redisTemplate;

    private AuthenticationManager authenticationManager;
    private SessionRegistry sessionRegistry;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, RedisTemplate<String, Object> redisTemplate,SessionRegistry sessionRegistry) {
        this.authenticationManager = authenticationManager;
        this.redisTemplate = redisTemplate;
        this.sessionRegistry = sessionRegistry;
        super.setFilterProcessesUrl("/auth/login");
        ConcurrentSessionControlAuthenticationStrategy strategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
        this.setSessionAuthenticationStrategy(strategy);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        if (request.getContentType().equals(MediaType.APPLICATION_JSON_UTF8_VALUE)
                || request.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            UsernamePasswordAuthenticationToken authRequest = null;
            try{
                LoginUser loginUser = new ObjectMapper().readValue(request.getInputStream(), LoginUser.class);
                rememberMe.set(loginUser.getRememberMe());
                authRequest = new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword(), new ArrayList<>());
                if(sessionRegistry.getAllPrincipals().contains(loginUser.getUsername())){
                    sessionRegistry.removeSessionInformation(request.getRequestedSessionId());
                }
                sessionRegistry.registerNewSession(request.getSession().getId(),authRequest.getPrincipal());

                return authenticationManager.authenticate(authRequest);
            } catch (IOException e) {
                return null;
            }
        }
        else {
            return super.attemptAuthentication(request, response);
        }
    }
    // 成功验证后调用的方法
    // 如果验证成功，就生成token并返回
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {

        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication );
         boolean isRemember = rememberMe.get() == 1;

        List<String> role = new ArrayList<>();
        Collection<? extends GrantedAuthority> authorities = jwtUser.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            role.add(authority.getAuthority());
        }

        String token = JwtTokenUtils.createToken(jwtUser, role, isRemember);
        // 返回创建成功的token
        // 但是这里创建的token只是单纯的token
        // 按照jwt的规定，最后请求的时候应该是 `Bearer token`
        String key = TokenConst.TOKEN_PREFIX + jwtUser.getId();
        response.setHeader("token", JwtTokenUtils.TOKEN_PREFIX + token);
        redisTemplate.opsForValue().set(key,token,TokenConst.JWT_TOKEN_EXPIRATION, TimeUnit.SECONDS);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        ResponseUtils.write(response, "authentication failed, reason: " + e.getMessage(), 403);
    }
}
