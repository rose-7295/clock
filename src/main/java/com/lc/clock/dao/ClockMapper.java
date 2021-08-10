package com.lc.clock.dao;

import com.lc.clock.pojo.Clock;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ClockMapper {
    /*int deleteByPrimaryKey(Long id);

    int insertSelective(Clock record);


    int updateByPrimaryKeySelective(Clock record);

    int updateByPrimaryKey(Clock record);*/

    //更新打卡表
    int updateClockById(Clock clock);

    //注册用户时，需要增加打卡表
    int addClock(Clock clock);

    //查找打卡表
    Clock selectClock(String nickname);

    //删除打卡表
    int deleteClock(String nickname);

    //获取所有打卡表
    List<Clock> findAllClock();
}