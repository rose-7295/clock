package com.lc.clock.controller;

import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import com.lc.clock.service.ManagerService;
import com.lc.clock.service.UserService;
import com.lc.clock.utils.ResultVOUtil;
import com.lc.clock.vo.DetailVO;
import com.lc.clock.vo.RankVO;
import com.lc.clock.vo.ResultVO;
import com.lc.clock.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ManagerService managerService;



    /**
     * 登录
     */
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

    /**
     * 注册
     */
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
            Clock clock = new Clock(nickname);
            userService.addClock(clock);

            return ResultVOUtil.success(null);
        }else{
            return ResultVOUtil.error("该用户已存在");
        }
    }

    /**
     * 修改个人信息
     */
    @PostMapping("/update")
    public ResultVO update(@RequestParam("nickname") String nickname,
                           @RequestParam("newpassword") String newpassword,
                           @RequestParam("grade") Integer grade,
                           @RequestParam("password") String password){
        //先查询数据库里是否有该用户
        User user = userService.selectByNickName(nickname);
        if (user != null){
            //说明数据库里存在该用户
            if (user.getPassword().equals(password)){
                user.setGrade(grade);
                user.setNickname(nickname);
                user.setPassword(newpassword);
                userService.updateUser(user);
                return ResultVOUtil.success(null);
            }else{
                log.error("【密码错误】修改失败");
                return ResultVOUtil.error("密码错误，修改失败");
            }

        }else{
            return ResultVOUtil.error("信息错误，修改失败");
        }
    }


    /**
     * 获取信息列表
     */
    @GetMapping("/info")
    public ResultVO info(@RequestParam("grade") Integer grade){
        if(grade == 0 || grade == 1){
            List<User> usersByGrade = userService.findByGrade(grade);
            List<UserVO> userVOList = new ArrayList<>();
            //把User转换为UserVO
            for(User user : usersByGrade){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                userVO.setAllTime(userService.selectByNickName(user.getNickname()).getAllTime());

                userVO.setFinishTime(managerService.findClock(user.getNickname()).getClockTime());
                userVOList.add(userVO);
            }
            return ResultVOUtil.success(userVOList);
        }else{
            log.error("【获取打卡信息】失败！不存在该年级");
            return ResultVOUtil.error("查找失败！不存在该年级");
        }
    }

    /**
     * 用户详情
     */
    @GetMapping("/detail")
    public ResultVO detail(@RequestParam("nickname") String nickname){
        Clock clock = managerService.findClock(nickname);
        User user = userService.selectByNickName(nickname);
        if (clock == null || user == null){
            log.error("【查找打卡表】不存在该用户");
            return ResultVOUtil.error("不存在该用户");
        }
        DetailVO detailVO = new DetailVO();
        BeanUtils.copyProperties(user,detailVO);
        BeanUtils.copyProperties(clock,detailVO);
        detailVO.setFinishTime(clock.getClockTime());
        return ResultVOUtil.success(detailVO);
    }

    /**
     * 开始打卡
     */
    @PostMapping("/start")
    public ResultVO start(@RequestParam("nickname") String nickname){
        Clock clock = managerService.findClock(nickname);//根据nickname在打卡表里找到记录
        if (clock == null){
            log.error("【开始打卡】失败，打卡表中无该用户数据");
            return ResultVOUtil.error("打卡失败！不存在该用户");
        }
        /*更新打卡表*/
        Date date = new Date();//获取当前时间
        clock.setStartTime(date);//设定开始打卡时间
        clock.setEndTime(null);//更新结束打卡时间
        managerService.updateClock(clock);//写进数据库

        /*更新用户打卡状态*/
        User user = userService.selectByNickName(nickname);
        user.setOnline(1);
        userService.updateUser(user);

        return ResultVOUtil.success(date);
    }


    /**
     * 结束打卡
     */
    @PostMapping("/end")
    public ResultVO end(@RequestParam("nickname") String nickname){
        Clock clock = managerService.findClock(nickname);
        if (clock == null){
            log.error("【结束打卡】失败，打卡表中无该用户");
            return ResultVOUtil.error("结束打卡失败！不存在该用户");
        }
        /*更新打卡表*/
        Date date = new Date();//date为结束打卡时间
        clock.setEndTime(date);
        List<Date> time = new ArrayList<>();
        time.add(clock.getStartTime());//clock.getStartTime()为开始打卡时间
        time.add(date);
        //将本次打卡时间写进数据库
        //计算打卡时间
        Long t = (date.getTime()-clock.getStartTime().getTime())/(1000*60*60);
        BigDecimal clockTime = new BigDecimal(t);
        //打卡时间超过六小时判断无效
        if (t >= 6){
            return ResultVOUtil.error("【打卡失败】打卡时间过长，无效");
        }

        //加进总时间
        clock.setClockTime(clock.getClockTime().add(clockTime));
        //加进每周打卡时间
        int day = DateUtils.dayOfWeek(date);
        switch (day){
            case 1 : clock.setSunTime(clock.getSunTime().add(clockTime));break;
            case 2 : clock.setMonTime(clock.getMonTime().add(clockTime));break;
            case 3 : clock.setTueTime(clock.getTueTime().add(clockTime));break;
            case 4 : clock.setWedTime(clock.getWedTime().add(clockTime));break;
            case 5 : clock.setThuTime(clock.getThuTime().add(clockTime));break;
            case 6 : clock.setFriTime(clock.getFriTime().add(clockTime));break;
            case 7 : clock.setSatTime(clock.getSatTime().add(clockTime));break;
        }
        managerService.updateClock(clock);

        /*更新用户打卡状态*/
        User user = userService.selectByNickName(nickname);
        user.setOnline(0);
        userService.updateUser(user);
        return ResultVOUtil.success(time);
    }

    /**
     * 图表
     */
    @GetMapping("/ranking")
    public ResultVO ranking(){
        List<User> userList = userService.findAllUser();
        List<RankVO> rankVOList = new ArrayList<>();
        for(User user : userList){
            RankVO rankVO = new RankVO();
            Clock clock = managerService.findClock(user.getNickname());
            BeanUtils.copyProperties(user,rankVO);

            //给一周中的每天赋值
            HashMap map = new HashMap();
            map.put("Mon",clock.getMonTime());
            map.put("Tues",clock.getTueTime());
            map.put("Wed",clock.getWedTime());
            map.put("Thur",clock.getTueTime());
            map.put("Fri",clock.getFriTime());
            map.put("Sat",clock.getSatTime());
            map.put("Sun",clock.getSunTime());
            rankVO.setDay(map);

            rankVO.setFinishTime(clock.getClockTime());
            rankVOList.add(rankVO);
        }
        return ResultVOUtil.success(rankVOList);
    }

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public ResultVO upload(@RequestParam("file") MultipartFile file,
                           @RequestParam("nickname") String nickname) throws IOException {
        User user = userService.selectByNickName(nickname);
        String filename = file.getOriginalFilename();
        String filePath = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\img";
        if (!new File(filePath).exists()){
            new File(filePath).mkdirs();
        }
        File dest = new File(filePath + File.separator + nickname +"_"+filename);
        try {
            file.transferTo(dest);
        }catch (Exception e){
            e.printStackTrace();
        };
        user.setAvatar("/img/" + nickname+"_"+filename);
        userService.updateUser(user);
        return ResultVOUtil.success("/img/" + nickname+"_"+filename);
    }

    /**
     * 删除图片
     */
    @PostMapping("/delete")
    public ResultVO delete(@RequestParam("nickname") String nickname){
        User user = userService.selectByNickName(nickname);
        if (user == null){
            log.error("【删除头像】失败，不存在该用户");
            return ResultVOUtil.error("不存在该用户");
        }
        user.setAvatar(null);
        userService.updateUser(user);
        return ResultVOUtil.success(null);
    }

}
