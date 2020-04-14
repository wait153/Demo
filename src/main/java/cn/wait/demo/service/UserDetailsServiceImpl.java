package cn.wait.demo.service;

import cn.wait.demo.entity.JwtUser;
import cn.wait.demo.entity.Role;
import cn.wait.demo.entity.User;
import cn.wait.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(s);
        List<String> roles = userMapper.findRoleByUser(user);
        user.setRole(roles);
        return new JwtUser(user);
    }

}
