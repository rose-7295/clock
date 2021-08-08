package com.lc.clock.service.impl;

import com.lc.clock.dao.UserMapper;
import com.lc.clock.pojo.User;
import com.lc.clock.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper mapper;

    @Override
    public User selectByNickName(String nickname) {
        User user = mapper.selectByNickName(nickname);
        return user;
    }

    @Override
    public int addUser(User user) {
        return mapper.addUser(user);
    }

    @Override
    public int updateUser(User user) {
        return mapper.updateUser(user);
    }
}
