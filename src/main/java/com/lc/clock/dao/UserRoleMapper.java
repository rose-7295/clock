package com.lc.clock.dao;

import com.lc.clock.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author 柴柴快乐每一天
 * @create 2021-09-01  7:27 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */

@Mapper
@Repository
public interface UserRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserRole record);

    int insertSelective(UserRole record);

    UserRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserRole record);

    int updateByPrimaryKey(UserRole record);

    Integer deleteByUserId(Long uid);

    Integer addRoles(@Param("uid") Long uid, @Param("rids") Integer[] rids);

    Integer addHrWithSalary(@Param("uid") Long uid, @Param("sids") Integer[] sids);
}
