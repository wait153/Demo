package cn.wait.demo.entity;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class User {

    private Integer id;

    private String username;

    private String password;

    private List<String> role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                username.equals(user.username) &&
                Objects.equals(password, user.password) &&
                Objects.equals(role, user.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role);
    }
}
