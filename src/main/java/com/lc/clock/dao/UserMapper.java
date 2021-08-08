package com.lc.clock.dao;

import com.lc.clock.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {
    /*
    int insertSelective(User record);

    int updateByPrimaryKeySelective(User record);
*/

    //添加新用户（注册）
    int addUser(User user);

    //删除用户
    int deleteUser(String nickname);

    //查询用户
    User selectByNickName(String nickname);

    //更新用户
    int updateUser(User user);

    //根据年级查询用户
    List<User> findByGrade(Integer grade);
}