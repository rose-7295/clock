package com.lc.clock.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lc.clock.pojo.Clean;
import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import com.lc.clock.pojo.Week;
import com.lc.clock.service.CleanService;
import com.lc.clock.service.ManagerService;
import com.lc.clock.service.UserService;
import com.lc.clock.service.WeekService;
import com.lc.clock.utils.IpUtil;
import com.lc.clock.utils.RespBean;
import com.lc.clock.utils.ResultVOUtil;
import com.lc.clock.utils.minio.MinioUtils;
import com.lc.clock.vo.DetailVO;
import com.lc.clock.vo.RankVO;
import com.lc.clock.vo.ResultVO;
import com.lc.clock.vo.UserVO;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.DateUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/user")
@Transactional
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private CleanService cleanService;

    @Autowired
    private WeekService weekService;

    @Value("${minio.host}")
    String nginxHost;





    /**
     * 修改个人信息
     */
    @PostMapping("/update")
    public ResultVO update(@RequestParam("username") String username,
                           @RequestParam("newpassword") String newpassword,
                           @RequestParam("grade") Integer grade,
                           @RequestParam("password") String password,
                           @RequestParam("avatar") String avatar){
        //先查询数据库里是否有该用户
        User user = userService.selectByUsername(username);

        if (user != null){
            //说明数据库里存在该用户
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String newPass = encoder.encode(newpassword);
            if (encoder.matches(password, user.getPassword())){
                user.setGrade(grade);
                user.setUsername(username);
                if (!avatar.equals(user.getAvatar())){
                    user.setAvatar(avatar);
                }
                user.setPassword(newPass);
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

    @PostMapping("/updatePwd")
    public RespBean updateHrPassById(@RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     @RequestParam("newpassword") String newpassword) {
        Integer result = userService.updatePwdByUsername(password, newpassword, username);
        if (result == 1) {
            return RespBean.ok("更新密码成功，请重新登录!");
        } else if (result == 0) {
            return RespBean.error("旧密码错误!");
        }
        return RespBean.error("更新密码失败,请联系管理员!");
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
                userVO.setAllTime(userService.selectByUsername(user.getUsername()).getAllTime());

                userVO.setFinishTime(managerService.findClock(user.getUsername()).getClockTime());
                userVOList.add(userVO);
            }
            userVOList.sort((x,y) -> x.getFinishTime().compareTo(y.getFinishTime()));
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
    public ResultVO detail(@RequestParam("username") String username){
        Clock clock = managerService.findClock(username);
        User user = userService.selectByUsername(username);
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
    public ResultVO start(HttpServletRequest request, @RequestParam("username") String username){
        Clock clock = managerService.findClock(username);//根据nickname在打卡表里找到记录
        if (clock == null){
            log.error("【开始打卡】失败，打卡表中无该用户数据");
            return ResultVOUtil.error("打卡失败！不存在该用户");
        }
        if (IpUtil.isLecIP(request) == 0) {
            return ResultVOUtil.error(2, "请连接LC_new / LC_new_5G!");
        } else if (IpUtil.isLecIP(request) == 2) {
            return ResultVOUtil.error(3, "请打开浏览器权限");
        }
        /*更新打卡表*/
        Date date = new Date();//获取当前时间
        clock.setStartTime(date);//设定开始打卡时间
        clock.setEndTime(null);//更新结束打卡时间
        managerService.updateClock(clock);//写进数据库

        /*更新用户打卡状态*/
        User user = userService.selectByUsername(username);
        user.setOnline(1);
        userService.updateUser(user);

        return ResultVOUtil.success(date);
    }


    /**
     * 结束打卡
     */
    @PostMapping("/end")
    public ResultVO end(HttpServletRequest request, @RequestParam("username") String username){
        Clock clock = managerService.findClock(username);
        if (clock == null){
            log.error("【结束打卡】失败，打卡表中无该用户");
            return ResultVOUtil.error("结束打卡失败！不存在该用户");
        }

        if (IpUtil.isLecIP(request) == 0) {
            return ResultVOUtil.error(2, "请连接LC_new / LC_new_5G!");
        } else if (IpUtil.isLecIP(request) == 2) {
            return ResultVOUtil.error(3, "请打开浏览器权限");
        }

        /*更新打卡表*/
        Date date = new Date();//date为结束打卡时间
        clock.setEndTime(date);
        List<Date> time = new ArrayList<>();
        time.add(clock.getStartTime());//clock.getStartTime()为开始打卡时间
        time.add(date);
        //将本次打卡时间写进数据库
        //计算打卡时间
        Double t = (date.getTime()-clock.getStartTime().getTime())*1.0/(1000*60*60*1.0);
        BigDecimal clockTime = new BigDecimal(t);
        //打卡时间超过六小时、小于半小时判断无效
        if (t >= 6 || t <= 0.5){
            return ResultVOUtil.error("【打卡失败】打卡时间无效");
        }

        //加进总时间
        clock.setClockTime(clock.getClockTime().add(clockTime));;
        //加进每周打卡时间
        int day = DateUtils.dayOfWeek(date);//判断今天是周几
        //System.out.println(day);
        switch (day){
            case 1 : clock.setSunTime(clock.getSunTime().add(clockTime));break;
            case 2 : clock.setMonTime(clock.getMonTime().add(clockTime));break;
            case 3 : clock.setTueTime(clock.getTueTime().add(clockTime));break;
            case 4 : clock.setWedTime(clock.getWedTime().add(clockTime));break;
            case 5 : clock.setThuTime(clock.getThuTime().add(clockTime));break;
            case 6 : clock.setFriTime(clock.getFriTime().add(clockTime));break;
            case 7 : clock.setSatTime(clock.getSatTime().add(clockTime));break;
            default:break;
        }

        managerService.updateClock(clock);

        /*更新用户打卡状态*/
        User user = userService.selectByUsername(username);
        user.setOnline(0);
        userService.updateUser(user);


        return ResultVOUtil.success(time);
    }

    /**
     * 只修改打卡状态
     * @param username
     * @return
     */
    @PostMapping("/changeOnline")
    public ResultVO changeOnline(@RequestParam("username") String username){
        User user = userService.selectByUsername(username);
        user.setOnline(0);
        userService.updateUser(user);
        return ResultVOUtil.success("【打卡失败】打卡时间无效");
    }

    /**
     * 图表
     */
    @GetMapping("/ranking")
    public ResultVO ranking(@RequestParam("grade") Integer grade){
        List<User> userList = userService.findAllUser();
        List<RankVO> rankVOList = new ArrayList<>();
        for(User user : userList){
            if(grade!=2 && !user.getGrade().equals(grade)){
                continue;
            }
            RankVO rankVO = new RankVO();
            Clock clock = managerService.findClock(user.getUsername());
            BeanUtils.copyProperties(user,rankVO);

            //给一周中的每天赋值
            HashMap map = new HashMap();
            map.put("1",clock.getMonTime());
            map.put("2",clock.getTueTime());
            map.put("3",clock.getWedTime());
            map.put("4",clock.getThuTime());
            map.put("5",clock.getFriTime());
            map.put("6",clock.getSatTime());
            map.put("7",clock.getSunTime());
            rankVO.setDay(map);

            rankVO.setFinishTime(clock.getClockTime());
            rankVOList.add(rankVO);
        }
        return ResultVOUtil.success(rankVOList);
    }

    @PostMapping("/avatar")
    public RespBean updateAvatar(@RequestParam("file") MultipartFile[] file,
                                 @RequestParam("username") String username, Authentication authentication) throws InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RespBean result = MinioUtils.upload(file);
        Map<String, Object> data = (Map<String, Object>) result.getData();
        String bucketName = (String) data.get("bucketName");
        List<String> filenames = (List<String>) data.get("fileName");
        String filename = filenames.get(0);

        String url = nginxHost + bucketName +"/"+filename;
        System.out.println("url===>"+url);

        if (userService.updateAvatar(url, username) == 1) {
            User user = (User) authentication.getPrincipal();
            user.setAvatar(url);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, authentication.getCredentials(), authentication.getAuthorities()));
            return RespBean.ok("更新成功!", url);
        }

        return RespBean.error("更换失败!");
    }

    /**
     * 上传图片
     */
    @PostMapping("/upload")
    public ResultVO upload(@RequestParam("file") MultipartFile file,
                           @RequestParam("username") String username){
        User user = userService.selectByUsername(username);
        String filename = file.getOriginalFilename();
        String filePath = System.getProperty("user.dir")+"/img";
        if (!new File(filePath).exists()){
            new File(filePath).mkdirs();
        }
        File dest = new File(filePath + File.separator +"_"+filename);
        try {
            file.transferTo(dest);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultVOUtil.success("/img/" + "_"+filename);
    }

    /**
     * 删除图片
     */
    @PostMapping("/delete")
    public ResultVO delete(@RequestParam("username") String username){
        User user = userService.selectByUsername(username);
        if (user == null){
            log.error("【删除头像】失败，不存在该用户");
            return ResultVOUtil.error("不存在该用户");
        }
        user.setAvatar(null);
        userService.updateUser(user);
        return ResultVOUtil.success(null);
    }

    /**
     * 打扫卫生
     */
    @GetMapping("/clean")
    public ResultVO clean() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date first = dateFormat.parse("2021-08-12 00:00:00");
        Date date = new Date();
        //两个时间点之间差了多少天
        long time = (date.getTime() - first.getTime()) / (1000 * 60 * 60 * 24);
        int week = (int) time/7;
        Clean clean = cleanService.findClean(week);
        //System.out.println(clean.getUsername());
        Map map = new HashMap();
        map.put("week",week);
        map.put("username",clean.getUsername());
        return ResultVOUtil.success(map);
    }

    /**
     * 每周打卡时间
     */
    @GetMapping("/week_rank")
    public ResultVO weekRank(@RequestParam("grade") Integer grade){
        List<User> userList = userService.findAllUser();
        List<RankVO> rankVOList = new ArrayList<>();
        for(User user : userList){
            if(grade!=2 && !user.getGrade().equals(grade)){
                continue;
            }
            try {
                Week week = weekService.findWeek(user.getUsername());

                Map<String,BigDecimal> map = new HashMap();
                if(week.getWeek()!=null){
                    String[] res = week.getWeek().split(",");
                    for (String s : res) {
                        try {
                            Gson gson = new Gson();
                            Map<String,BigDecimal> map1 = gson.fromJson(s,new TypeToken<Map<String,BigDecimal>>(){}.getType());
                            map.putAll(map1);
                        }catch (JsonSyntaxException e){}
                    }
                }


                //System.out.println("map = " + map);
                Clock clock = managerService.findClock(user.getNickname());
                RankVO rankVO = new RankVO(map,clock.getClockTime(),user.getAllTime(), user.getUsername(), user.getNickname());
                rankVOList.add(rankVO);
            }catch (Exception e){}

        }
        return ResultVOUtil.success(rankVOList);
    }
}
