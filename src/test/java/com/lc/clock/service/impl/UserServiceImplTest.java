package com.lc.clock.service.impl;

import com.lc.clock.pojo.User;
import com.lc.clock.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void selectByNickName() {
        User user = userService.selectByNickName("猫宁");
        System.out.println(user);
    }

    @Test
    void addUser() {
    }
}