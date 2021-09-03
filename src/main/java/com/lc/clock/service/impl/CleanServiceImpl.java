package com.lc.clock.service.impl;

import com.lc.clock.dao.CleanMapper;
import com.lc.clock.pojo.Clean;
import com.lc.clock.service.CleanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class CleanServiceImpl implements CleanService {

    @Autowired
    private CleanMapper mapper;

    @Override
    public Clean findClean(Integer week) {
        return mapper.findUser(week);
    }
}
