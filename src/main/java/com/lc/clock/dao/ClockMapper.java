package com.lc.clock.dao;

import com.lc.clock.pojo.Clock;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ClockMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Clock record);

    int insertSelective(Clock record);

    Clock selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Clock record);

    int updateByPrimaryKey(Clock record);
}