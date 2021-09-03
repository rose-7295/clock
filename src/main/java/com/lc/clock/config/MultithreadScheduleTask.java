package com.lc.clock.config;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.lc.clock.pojo.Clock;
import com.lc.clock.pojo.User;
import com.lc.clock.pojo.Week;
import com.lc.clock.service.ManagerService;
import com.lc.clock.service.UserService;
import com.lc.clock.service.WeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling   // 1.开启定时任务
public class MultithreadScheduleTask {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private WeekService weekService;

    @Scheduled(cron = "0 0 0 ? * MON")
    public void flush() throws ParseException {
        int week = 1;
        List<Clock> clockList = managerService.findAllClock();
        for(Clock clock : clockList){
            //把打卡时间写进week
            try {
                Map map = new HashMap();
                map.put(String.valueOf(week),clock.getClockTime());

                String res = JSONObject.toJSONString(map);

                Week w = weekService.findWeek(clock.getUsername());
                if (w.getWeek() == null){
                    w.setWeek(res);
                }else{
                    w.setWeek(w.getWeek()+","+res);
                }
                weekService.updateWeek(w);

                clock.setClockTime(new BigDecimal(0));
                clock.setMonTime(new BigDecimal(0));
                clock.setTueTime(new BigDecimal(0));
                clock.setWedTime(new BigDecimal(0));
                clock.setThuTime(new BigDecimal(0));
                clock.setFriTime(new BigDecimal(0));
                clock.setSatTime(new BigDecimal(0));
                clock.setSunTime(new BigDecimal(0));
                //刷新数据库
                managerService.updateClock(clock);
            }catch (Exception e){}

        }
        week ++;
    }

}
