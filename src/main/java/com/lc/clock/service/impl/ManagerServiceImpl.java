package com.lc.clock.service.impl;

import com.lc.clock.dao.ClockMapper;
import com.lc.clock.dao.UserMapper;
import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import com.lc.clock.service.ManagerService;
import com.lc.clock.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ClockMapper clockMapper;


    @Override
    public int updateClock(Clock clock) {
        return clockMapper.updateClock(clock);
    }

    @Override
    public Clock findClock(String username) {
        return clockMapper.selectClock(username);
    }

    @Override
    public int deleteClock(String username) {
        return clockMapper.deleteClock(username);
    }

    @Override
    public int deleteUser(String username) {
        return userMapper.deleteUser(username);
    }

    @Override
    public List<Clock> findAllClock() {
        return clockMapper.findAllClock();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        user.setRoles(userMapper.getUserRolesById(user.getId()));
        return user;
    }
}
