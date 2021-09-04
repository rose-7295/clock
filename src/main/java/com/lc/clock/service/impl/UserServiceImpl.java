package com.lc.clock.service.impl;

import com.lc.clock.dao.ClockMapper;
import com.lc.clock.dao.UserMapper;
import com.lc.clock.dao.UserRoleMapper;
import com.lc.clock.dao.WeekMapper;
import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import com.lc.clock.pojo.Week;
import com.lc.clock.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private WeekMapper weekMapper;


    @Override
    public User selectByUsername(String username) {
        User user = mapper.selectByUsername(username);
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
        return mapper.updateByPrimaryKeySelective(user);
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

    @Override
    public int updateClockByWeek(Clock clock) {
        Clock newClock = new Clock();
        newClock.setId(clock.getId());
        newClock.setStartTime(clock.getStartTime());
        newClock.setEndTime(clock.getEndTime());
        newClock.setUsername(clock.getUsername());
        return clockMapper.updateClock(clock);
    }

    @Override
    @Transactional(rollbackFor = {})
    public int register(String nickname, String username, Integer grade, String password) {
        int result = 0;
        if (userMapper.selectByUsername(username) != null) {
            result = 2;
        }
        User user = new User(nickname, username, password, grade);
        user.setEnabled(true);
        if (user.getGrade() == 0) {
            user.setAllTime(new BigDecimal(28));
        } else {
            user.setAllTime(new BigDecimal(38));
        }
        String defaultAvatar = "https://chasing1874.oss-cn-chengdu.aliyuncs.com/20210809074643.png";
        user.setAvatar(defaultAvatar);
        // 加密密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPwd = encoder.encode(user.getPassword());
        user.setPassword(encodedPwd);

        result = userMapper.insertSelective(user);

        if (result == 1) {
            // 创建打卡表
            clockMapper.addClock(new Clock(username));
            // 创建周表
            weekMapper.addWeek(new Week(user.getUsername(), null));
            // 分配默认角色
            Integer[] defaultRole = {2};
            updateUserRole(user.getId(), defaultRole);
        }
        // 添加角色

        return result;

    }

    @Transactional(rollbackFor = {})
    @Override
    public boolean updateUserRole(Long uid, Integer[] rids) {
        userRoleMapper.deleteByUserId(uid);
        return userRoleMapper.addRoles(uid, rids) == rids.length;
    }

    @Override
    public Integer deleteRole(Long uid) {
        return userRoleMapper.deleteByUserId(uid);
    }

    @Override
    public Integer updatePwdByUsername(String password, String newpassword, String username) {
        User user = userMapper.selectByUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (encoder.matches(password, user.getPassword())) {
            String encodePass = encoder.encode(newpassword);
            Integer result = userMapper.updatePwdByUsername(username, encodePass);
            if (result == 1) {
                // 更新成功
                return 1;
            }
            // 密码更新失败
            return -1;
        }

        // 旧密码错误
        return 0;
    }

    @Override
    public Integer updateAvatar(String url, String username) {
        return userMapper.updateAvatar(url, username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = mapper.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        user.setRoles(mapper.getUserRolesById(user.getId()));
        return user;
    }
}
