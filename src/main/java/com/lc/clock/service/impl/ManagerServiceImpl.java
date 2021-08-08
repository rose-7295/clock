package com.lc.clock.service.impl;

import com.lc.clock.dao.ClockMapper;
import com.lc.clock.dao.UserMapper;
import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import com.lc.clock.service.ManagerService;
import com.lc.clock.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
        return clockMapper.updateClockByNickName(clock);
    }

    @Override
    public Clock findClock(String nickname) {
        return clockMapper.selectClock(nickname);
    }

    @Override
    public int deleteClock(String nickname) {
        return clockMapper.deleteClock(nickname);
    }

    @Override
    public int deleteUser(String nickname) {
        return userMapper.deleteUser(nickname);
    }
}
