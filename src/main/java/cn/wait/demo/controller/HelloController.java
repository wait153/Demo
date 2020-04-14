package cn.wait.demo.controller;

import cn.wait.demo.utils.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @RequestMapping("/word")
    public String listTasks(){

        return "helloWord";
    }


}
