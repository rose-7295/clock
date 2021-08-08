package com.lc.clock.controller;

import com.lc.clock.pojo.User;
import com.lc.clock.service.UserService;
import com.lc.clock.utils.ResultVOUtil;
import com.lc.clock.vo.ResultVO;
import com.lc.clock.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResultVO login(@RequestParam("nickname") String nickname,
                          @RequestParam("password") String password){
        User user = userService.selectByNickName(nickname);
        if (user == null){
            log.error("【登录】用户不存在");
            return new ResultVO(1,"用户不存在",null);
        }
        //判断密码
        if (!user.getPassword().equals(password)){
            //密码错误
            log.error("【登录】密码错误");
            return ResultVOUtil.error("密码错误");
        }
        //密码相等，返回相关数据
        return ResultVOUtil.success(user);
    }

    @PostMapping("/regis")
    public ResultVO regis(@RequestParam("nickname") String nickname,
                          @RequestParam("username") String username,
                          @RequestParam("grade") Integer grade,
                          @RequestParam("password") String password){
        //先查询数据库里是否有该用户
        User user = userService.selectByNickName(nickname);
        if (user == null){
            User newuser = new User();

            newuser.setNickname(nickname);
            newuser.setUsername(username);
            newuser.setPassword(password);
            newuser.setGrade(grade);

            userService.addUser(newuser);
            return ResultVOUtil.success();
        }else{
            return ResultVOUtil.error("该用户已存在");
        }
    }

    @PostMapping("/update")
    public ResultVO update(@RequestParam("nickname") String nickname,
                           @RequestParam("username") String username,
                           @RequestParam("grade") Integer grade,
                           @RequestParam("password") String password){
        //先查询数据库里是否有该用户
        User user = userService.selectByNickName(nickname);
        if (user != null){
            //说明数据库里存在该用户
            user.setGrade(grade);
            user.setNickname(nickname);
            user.setUsername(username);
            user.setPassword(password);
            user.setUpdateTime(new Date());

            return ResultVOUtil.success(userService.updateUser(user));
        }else{
            return ResultVOUtil.error("信息错误，修改失败");
        }
    }
}
