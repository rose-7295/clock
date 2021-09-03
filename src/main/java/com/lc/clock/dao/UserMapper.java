package com.lc.clock.dao;

import com.lc.clock.pojo.Role;
import com.lc.clock.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {

    int insertSelective(User record);

    int updateByPrimaryKeySelective(User record);


    //添加新用户（注册）
    int addUser(User user);

    //删除用户
    int deleteUser(String username);

    //查询用户
    User selectByUsername(String username);

    //更新用户
    int updateUser(User user);

    //根据年级查询用户
    List<User> findByGrade(Integer grade);

    //获取所有用户
    List<User> findAllUser();

    User loadUserByUsername(String username);

    List<Role> getUserRolesById(Long id);

    Integer updatePwdByUsername(@Param("username") String username, @Param("encodePass") String encodePass);

    Integer updateAvatar(@Param("url") String url, @Param("username") String username);
}