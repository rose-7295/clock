package com.lc.clock.service;

import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface UserService extends UserDetailsService {
    User selectByUsername(String username);

    int addUser(User user);

    int updateUser(User user);

    int addClock(Clock clock);

    List<User> findByGrade(Integer grade);

    List<User> findAllUser();

//    @Override
//    default UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//        return null;
//    }

    int updateClockByWeek(Clock clock);


    int register(String nickname, String username, Integer grade, String password);

    boolean updateUserRole(Long uid, Integer[] rids);

    Integer deleteRole(Long uid);

    Integer updatePwdByUsername(String password, String newpassword, String username);

    Integer updateAvatar(String url, String username);
}
