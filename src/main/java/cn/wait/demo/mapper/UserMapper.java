package cn.wait.demo.mapper;

import cn.wait.demo.entity.Role;
import cn.wait.demo.entity.User;
import cn.wait.demo.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(String username);
    int saveUser(User user);

    void saveUserRole(@Param("list")List<UserRole> list);

    List<String> findRoleByUser(User user);
}
