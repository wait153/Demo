package cn.wait.demo.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Data
public class JwtUser implements UserDetails {

    private Integer id;
    private String userName;
    private String passWord;
    private Collection<? extends GrantedAuthority> authorities;

    public JwtUser() {
    }

    // 写一个能直接使用user创建jwtUser的构造器
    public JwtUser(User user) {
        id = user.getId();
        userName = user.getUsername();
        passWord = user.getPassword();
        List<String> role = user.getRole();
        List<SimpleGrantedAuthority> s = new ArrayList<>();
        for (int i = 0; i < role.size(); i++) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.get(i));
            s.add(simpleGrantedAuthority);
        }
        authorities = s;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passWord;

    }

    @Override
    public String getUsername() {
        return userName;

    }


    /**
     * 账户是否未过期
     *
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     *
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 账户凭证是否未过期
     *
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof User){
            return userName.equals(((User)o).getUsername());
        }
        if(o instanceof JwtUser){
            return userName.equals(((JwtUser)o).getUsername());
        }else{
            JwtUser jwtUser = (JwtUser) o;
            return id.equals(jwtUser.id) &&
                    userName.equals(jwtUser.userName) &&
                    passWord.equals(jwtUser.passWord) &&
                    authorities.equals(jwtUser.authorities);
        }
    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }
}
