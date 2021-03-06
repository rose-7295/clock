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
     * ??????????????????
     */
    @PostMapping("/update")
    public ResultVO update(@RequestParam("username") String username,
                           @RequestParam("newpassword") String newpassword,
                           @RequestParam("grade") Integer grade,
                           @RequestParam("password") String password,
                           @RequestParam("avatar") String avatar){
        //???????????????????????????????????????
        User user = userService.selectByUsername(username);

        if (user != null){
            //?????????????????????????????????
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
                log.error("??????????????????????????????");
                return ResultVOUtil.error("???????????????????????????");
            }

        }else{
            return ResultVOUtil.error("???????????????????????????");
        }
    }

    @PostMapping("/updatePwd")
    public RespBean updateHrPassById(@RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     @RequestParam("newpassword") String newpassword) {
        Integer result = userService.updatePwdByUsername(password, newpassword, username);
        if (result == 1) {
            return RespBean.ok("????????????????????????????????????!");
        } else if (result == 0) {
            return RespBean.error("???????????????!");
        }
        return RespBean.error("??????????????????,??????????????????!");
    }


    /**
     * ??????????????????
     */
    @GetMapping("/info")
    public ResultVO info(@RequestParam("grade") Integer grade){
        if(grade == 0 || grade == 1){
            List<User> usersByGrade = userService.findByGrade(grade);
            List<UserVO> userVOList = new ArrayList<>();
            //???User?????????UserVO
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
            log.error("???????????????????????????????????????????????????");
            return ResultVOUtil.error("?????????????????????????????????");
        }
    }

    /**
     * ????????????
     */
    @GetMapping("/detail")
    public ResultVO detail(@RequestParam("username") String username){
        Clock clock = managerService.findClock(username);
        User user = userService.selectByUsername(username);
        if (clock == null || user == null){
            log.error("???????????????????????????????????????");
            return ResultVOUtil.error("??????????????????");
        }
        DetailVO detailVO = new DetailVO();
        BeanUtils.copyProperties(user,detailVO);
        BeanUtils.copyProperties(clock,detailVO);
        detailVO.setFinishTime(clock.getClockTime());
        return ResultVOUtil.success(detailVO);
    }

    /**
     * ????????????
     */
    @PostMapping("/start")
    public ResultVO start(HttpServletRequest request, @RequestParam("username") String username){
        Clock clock = managerService.findClock(username);//??????nickname???????????????????????????
        if (clock == null){
            log.error("?????????????????????????????????????????????????????????");
            return ResultVOUtil.error("?????????????????????????????????");
        }
        if (IpUtil.isLecIP(request) == 0) {
            return ResultVOUtil.error(2, "?????????LC_new / LC_new_5G!");
        } else if (IpUtil.isLecIP(request) == 2) {
            return ResultVOUtil.error(3, "????????????????????????");
        }
        /*???????????????*/
        Date date = new Date();//??????????????????
        clock.setStartTime(date);//????????????????????????
        clock.setEndTime(null);//????????????????????????
        managerService.updateClock(clock);//???????????????

        /*????????????????????????*/
        User user = userService.selectByUsername(username);
        user.setOnline(1);
        userService.updateUser(user);

        return ResultVOUtil.success(date);
    }


    /**
     * ????????????
     */
    @PostMapping("/end")
    public ResultVO end(HttpServletRequest request, @RequestParam("username") String username){
        Clock clock = managerService.findClock(username);
        if (clock == null){
            log.error("???????????????????????????????????????????????????");
            return ResultVOUtil.error("???????????????????????????????????????");
        }

        if (IpUtil.isLecIP(request) == 0) {
            return ResultVOUtil.error(2, "?????????LC_new / LC_new_5G!");
        } else if (IpUtil.isLecIP(request) == 2) {
            return ResultVOUtil.error(3, "????????????????????????");
        }

        /*???????????????*/
        Date date = new Date();//date?????????????????????
        clock.setEndTime(date);
        List<Date> time = new ArrayList<>();
        time.add(clock.getStartTime());//clock.getStartTime()?????????????????????
        time.add(date);
        //????????????????????????????????????
        //??????????????????
        Double t = (date.getTime()-clock.getStartTime().getTime())*1.0/(1000*60*60*1.0);
        BigDecimal clockTime = new BigDecimal(t);
        //?????????????????????????????????????????????????????????
        if (t >= 6 || t <= 0.5){
            return ResultVOUtil.error("????????????????????????????????????");
        }

        //???????????????
        clock.setClockTime(clock.getClockTime().add(clockTime));;
        //????????????????????????
        int day = DateUtils.dayOfWeek(date);//?????????????????????
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

        /*????????????????????????*/
        User user = userService.selectByUsername(username);
        user.setOnline(0);
        userService.updateUser(user);


        return ResultVOUtil.success(time);
    }

    /**
     * ?????????????????????
     * @param username
     * @return
     */
    @PostMapping("/changeOnline")
    public ResultVO changeOnline(@RequestParam("username") String username){
        User user = userService.selectByUsername(username);
        user.setOnline(0);
        userService.updateUser(user);
        return ResultVOUtil.success("????????????????????????????????????");
    }

    /**
     * ??????
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

            //???????????????????????????
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
            return RespBean.ok("????????????!", url);
        }

        return RespBean.error("????????????!");
    }

    /**
     * ????????????
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
     * ????????????
     */
    @PostMapping("/delete")
    public ResultVO delete(@RequestParam("username") String username){
        User user = userService.selectByUsername(username);
        if (user == null){
            log.error("?????????????????????????????????????????????");
            return ResultVOUtil.error("??????????????????");
        }
        user.setAvatar(null);
        userService.updateUser(user);
        return ResultVOUtil.success(null);
    }

    /**
     * ????????????
     */
    @GetMapping("/clean")
    public ResultVO clean() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date first = dateFormat.parse("2021-08-30 00:00:00");
        Date date = new Date();
        //????????????????????????????????????
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
     * ??????????????????
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
