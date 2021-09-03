package com.lc.clock.service.impl;

import com.lc.clock.dao.WeekMapper;
import com.lc.clock.pojo.Week;
import com.lc.clock.service.WeekService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class WeekServiceImpl implements WeekService {

    @Autowired
    private WeekMapper mapper;

    @Override
    public Week findWeek(String username) {
        return mapper.selectWeek(username);
    }

    @Override
    public int updateWeek(Week week) {
        return mapper.updateWeek(week);
    }

    @Override
    public int deleteWeek(String username) {
        return mapper.deleteWeek(username);
    }

    @Override
    public int addWeek(Week week) {
        return mapper.addWeek(week);
    }
}
