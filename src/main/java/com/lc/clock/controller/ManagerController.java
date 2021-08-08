package com.lc.clock.controller;

import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import com.lc.clock.service.ManagerService;
import com.lc.clock.service.UserService;
import com.lc.clock.utils.ResultVOUtil;
import com.lc.clock.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/manage")
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    @Autowired
    private UserService userService;

    /**
     * 修改用户信息
     */
    @RequestMapping("/update")
    public ResultVO update(@RequestParam("nickname") String nickname, @RequestParam("username") String username,
                           @RequestParam("grade") Integer grade, @RequestParam("online") Integer online,
                           @RequestParam("allTime") BigDecimal allTime,
                           @RequestParam("finishTime") BigDecimal finishTime,
                           @RequestParam("role") String role,@RequestParam("newNickname") String newNickname
                           ){
        User user = userService.selectByNickName(nickname);
        if(user == null){
            log.error("【管理员修改信息】该用户不存在，修改失败");
            return ResultVOUtil.error("该用户不存在，修改失败");
        }
        //修改用户表
        user.setUsername(username);
        user.setNickname(newNickname);
        user.setGrade(grade);
        user.setOnline(online);
        user.setAllTime(allTime);
        user.setRole(role);
        userService.updateUser(user);

        //修改打卡表
        Clock clock = managerService.findClock(nickname);
        clock.setNickname(newNickname);
        clock.setClockTime(finishTime);
        managerService.updateClock(clock);

        return ResultVOUtil.success();
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    public ResultVO delete(@RequestParam("nickname") String nickname){
        User user = userService.selectByNickName(nickname);
        if(user == null){
            log.error("【删除用户】删除失败，不存在该用户");
            return ResultVOUtil.error("删除失败，用户不存在！");
        }
        managerService.deleteClock(nickname);
        managerService.deleteUser(nickname);

        return ResultVOUtil.success();
    }


}
