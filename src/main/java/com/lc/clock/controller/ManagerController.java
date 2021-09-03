package com.lc.clock.controller;

import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import com.lc.clock.pojo.Week;
import com.lc.clock.service.ManagerService;
import com.lc.clock.service.UserService;
import com.lc.clock.service.WeekService;
import com.lc.clock.utils.ResultVOUtil;
import com.lc.clock.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.softus.cdi.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.DateUtils;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/manage")
public class ManagerController {
    @Autowired
    private ManagerService managerService;

    @Autowired
    private UserService userService;

    @Autowired
    private WeekService weekService;

    /**
     * 修改用户信息
     */
    @Transactional
    @RequestMapping("/update")
    public ResultVO update(@RequestParam("username") String username,
                           @RequestParam("grade") Integer grade, @RequestParam("online") Integer online,
                           @RequestParam("allTime") BigDecimal allTime,
                           @RequestParam("finishTime") BigDecimal finishTime,
                           @RequestParam("role") String role,@RequestParam("newNickname") String newNickname
                           ){
        int res = 0;
        User user = userService.selectByUsername(username);
        if(user == null){
            log.error("【管理员修改信息】该用户不存在，修改失败");
            return ResultVOUtil.error("该用户不存在，修改失败");
        }
//        if (userService.selectByUsername(newNickname)!=null&& !userService.selectByUsername(newNickname).getId().equals(user.getId())){
//            return ResultVOUtil.error("该用户名已存在");
//        }
        if (!online.equals(user.getOnline())){
            if(online == 1){
                //未打卡变成打卡
                res = 2;
            }else{
                //打卡变成未打卡
                res = 1;
            }
        }
        //修改用户表
        user.setUsername(username);
        user.setNickname(newNickname);
        user.setGrade(grade);
        user.setOnline(online);
        user.setAllTime(allTime);
        user.setRole(role);

        //修改打卡表
        Clock clock = managerService.findClock(username);
        if (clock == null){
            return ResultVOUtil.error("没有该用户的打卡表");
        }
        clock.setUsername(user.getUsername());
        clock.setClockTime(finishTime);


        //修改周表
        Week week = weekService.findWeek(user.getUsername());
        if (week == null){
            return ResultVOUtil.error("没有该用户的周表");
        }

        if (userService.updateUser(user)==1 && managerService.updateClock(clock)==1 && weekService.updateWeek(week)==1){
            return ResultVOUtil.success(res);
        }else{
            return ResultVOUtil.error("数据库更新失败！");
        }
    }

    /**
     * 删除用户
     */
    @Transactional
    @PostMapping("/delete")
    public ResultVO delete(@RequestParam("username") String username){
        User user = userService.selectByUsername(username);
        if(user == null){
            log.error("【删除用户】删除失败，不存在该用户");
            return ResultVOUtil.error("删除失败，用户不存在！");
        }
        managerService.deleteClock(username);
        weekService.deleteWeek(username);
        userService.deleteRole(user.getId());
        managerService.deleteUser(username);

        return ResultVOUtil.success(null);
    }

}
