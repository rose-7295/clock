package com.lc.clock.service;

import com.lc.clock.pojo.Week;

public interface WeekService {
    Week findWeek(String username);

    int updateWeek(Week week);

    int deleteWeek(String username);

    int addWeek(Week week);
}
