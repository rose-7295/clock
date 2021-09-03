package com.lc.clock.service;

import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface ManagerService extends UserDetailsService {
    int updateClock(Clock clock);

    Clock findClock(String username);

    int deleteClock(String username);

    int deleteUser(String username);

    List<Clock> findAllClock();

}
