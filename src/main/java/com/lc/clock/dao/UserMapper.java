package com.lc.clock.dao;

import com.lc.clock.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

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
    int deleteByNickName(String nickname);

    //查询用户
    User selectByNickName(String nickname);

    //更新用户
    int updateUser(User user);
}