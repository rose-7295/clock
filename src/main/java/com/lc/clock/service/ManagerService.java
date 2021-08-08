package com.lc.clock.service;

import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;

public interface ManagerService {
    int updateClock(Clock clock);

    Clock findClock(String nickname);

    int deleteClock(String nickname);

    int deleteUser(String nickname);
}