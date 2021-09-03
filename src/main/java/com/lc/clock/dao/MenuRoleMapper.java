package com.lc.clock.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-30  10:02 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */

@Mapper
@Repository
public interface MenuRoleMapper {
    void deleteByRid(Integer rid);

    Integer insertRecord(Integer rid, Integer[] mids);
}
