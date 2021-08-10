package com.lc.clock.service.impl;

import com.lc.clock.dao.ClockMapper;
import com.lc.clock.dao.UserMapper;
import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import com.lc.clock.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper mapper;

    @Autowired
    private ClockMapper clockMapper;

    @Override
    public User selectByNickName(String nickname) {
        User user = mapper.selectByNickName(nickname);
        return user;
    }

    @Override
    public int addUser(User user) {
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);
        return mapper.addUser(user);
    }

    @Override
    public int updateUser(User user) {
        user.setUpdateTime(new Date());
        return mapper.updateUser(user);
    }

    @Override
    public int addClock(Clock clock) {
        return clockMapper.addClock(clock);
    }

    @Override
    public List<User> findByGrade(Integer grade) {
        return mapper.findByGrade(grade);
    }

    @Override
    public List<User> findAllUser() {
        return mapper.findAllUser();
    }
}
