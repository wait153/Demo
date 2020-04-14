package cn.wait.demo.config;

import cn.wait.demo.exception.JWTAccessDeniedHandler;
import cn.wait.demo.exception.JWTAuthenticationEntryPoint;
import cn.wait.demo.filter.JWTAuthenticationFilter;
import cn.wait.demo.filter.JWTAuthorizationFilter;
import cn.wait.demo.handler.MyLogoutHandler;
import cn.wait.demo.handler.MyLogoutSuccessHandler;
import cn.wait.demo.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.session.SimpleRedirectSessionInformationExpiredStrategy;
import org.springframework.web.cors.CorsUtils;

import javax.annotation.Resource;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    BackdoorAuthenticationProvider backdoorAuthenticationProvider;

    @Autowired
    private MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Autowired
    private MyLogoutHandler myLogoutHandler;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SessionRegistry sessionRegistry;
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
        auth.authenticationProvider(backdoorAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()//跨域支持
                .authorizeRequests()//允许基于使用HttpServletRequest限制访问
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()//放过预检请求
                .antMatchers(HttpMethod.DELETE, "/tasks/**").hasRole("ADMIN")
                // 测试用资源，需要验证了的用户才能访问
                .antMatchers("/tasks/**").authenticated()//认证过的请求才可以访问
                // 其他都放行了
                .anyRequest().permitAll()

                .and()
                .logout()//退出登录
                .invalidateHttpSession(true)
//                .addLogoutHandler(myLogoutHandler)
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .deleteCookies("JSESSIONID")
                .permitAll()

                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(),redisTemplate,sessionRegistry()))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(),redisTemplate))
                .addFilterAt(new ConcurrentSessionFilter(sessionRegistry,sessionInformationExpiredStrategy()),ConcurrentSessionFilter.class)
                //addFilterAt(Filter A,Filter B)//在指定的FilterB的位置添加过滤器A
                .exceptionHandling()//允许配置错误处理
                .authenticationEntryPoint(new JWTAuthenticationEntryPoint())//认证异常处理
                .accessDeniedHandler(new JWTAccessDeniedHandler())//访问权限异常处理

                .and()
                .sessionManagement()//会话设置
                .maximumSessions(1)//单用户登录
                .sessionRegistry(sessionRegistry())
                .expiredSessionStrategy(new SessionInformationExpiredStrategyImpl())//多用户登录异常处理
                ;
    }
    @Bean
    SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {	//(5)
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }
    private SessionInformationExpiredStrategy sessionInformationExpiredStrategy() {
        return new SimpleRedirectSessionInformationExpiredStrategy("/login");
    }
}
