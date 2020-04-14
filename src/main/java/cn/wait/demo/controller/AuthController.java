package cn.wait.demo.controller;

import cn.wait.demo.entity.User;
import cn.wait.demo.entity.UserRole;
import cn.wait.demo.mapper.UserMapper;
import cn.wait.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/register")
    public String registerUser(@RequestBody Map<String,String> registerUser){
        User user = new User();
        user.setUsername(String.valueOf(registerUser.get("username")));
        user.setPassword(bCryptPasswordEncoder.encode(String.valueOf(registerUser.get("password"))));
        String role = registerUser.get("role");
        List<String> roles = Arrays.asList(role.split(","));
        userMapper.saveUser(user);
        List<UserRole> ur = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++) {
            UserRole userRole = new UserRole(user.getId(), Integer.valueOf(roles.get(i)));
            ur.add(userRole);
        }
        userMapper.saveUserRole(ur);
        return user.toString();
    }

}
