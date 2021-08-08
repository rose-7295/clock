package com.lc.clock.service;

import com.lc.clock.pojo.User;

public interface UserService {
    User selectByNickName(String nickname);

    int addUser(User user);

    int updateUser(User user);
}
