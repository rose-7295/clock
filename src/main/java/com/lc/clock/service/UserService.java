package com.lc.clock.service;

import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;

import java.util.List;

public interface UserService {
    User selectByNickName(String nickname);

    int addUser(User user);

    int updateUser(User user);

    int addClock(Clock clock);

    List<User> findByGrade(Integer grade);
}
