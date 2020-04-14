package cn.wait.demo.utils;

import cn.wait.demo.entity.JwtUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

public class JwtTokenUtils {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_KEY_USER_ID = "id";
    public static final String JWT_USER_NAME = "userName";
    public static final String JWT_PASS_WORD = "passWord";
    private static final String SECRET = "jwtsecretdemo";
    private static final String ISS = "echisan";
    private static final String CREATE_TIME = "create_time";

    // 角色的key
    private static final String ROLE_CLAIMS = "rol";

    // 过期时间是3600秒，既是1个小时
    private static final long EXPIRATION = 3600L;

    // 选择了记住我之后的过期时间为7天
    private static final long EXPIRATION_REMEMBER = 604800L;

    // 创建token
    public static String createToken(JwtUser user, List<String> role, boolean isRememberMe) {
        long expiration = isRememberMe ? EXPIRATION_REMEMBER : EXPIRATION;
        HashMap<String, Object> map = new HashMap<>();
        map.put(ROLE_CLAIMS, role);
        map.put(JWT_KEY_USER_ID, user.getId());
        map.put(JWT_USER_NAME, user.getUsername());
        map.put(JWT_PASS_WORD, user.getPassword());
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .setClaims(map)
                .setIssuer(ISS)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .compact();
    }

    /**
     * 根据token获取JwtUser
     *
     * @param token
     * @return
     */
    public static JwtUser getUserFromToken(String token) {
        JwtUser user;
        try {
            final Claims claims = getClaimsFromToken(token);
            user = new JwtUser();
            user.setId(claims.get(JWT_KEY_USER_ID, Integer.class));
            user.setPassWord(claims.get(JWT_PASS_WORD, String.class));
            user.setUserName(claims.get(JWT_USER_NAME, String.class));
            List<String> list = claims.get(ROLE_CLAIMS, List.class);
            List<SimpleGrantedAuthority> sa = new ArrayList<>();
            for (String s : list) {
                sa.add(new SimpleGrantedAuthority(s));
            }
            user.setAuthorities(sa);

        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    // 从token中获取用户名
    public static String getUsername(String token) {
        return getTokenBody(token).getSubject();
    }

    // 获取用户角色
    public static List<String> getUserRole(String token) {
        return getTokenBody(token).get(ROLE_CLAIMS, List.class);
    }

    //设置过期
    public static void setExpiration(String token) {
           getTokenBody(token).setExpiration(new Date());
    }
    // 是否已过期
    public static boolean isExpiration(String token) {
        try {
            return getTokenBody(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    private static Claims getTokenBody(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String refreshToken(String token) {
        final Claims claims = getClaimsFromToken(token);
        claims.put(CREATE_TIME, new Date());
        return generateToken(claims);
    }

    private static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * 生成token时间 = 当前时间 + expiration（properties中配置的失效时间）
     *
     * @return
     */
    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + EXPIRATION * 1000);
    }

    /**
     * token获取用户信息
     *
     * @param accessToken
     * @return
     */
    public static Claims getClaimsFromToken(String accessToken) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }
}