package com.lc.clock.dao;

import com.lc.clock.pojo.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-30  9:57 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */

@Mapper
@Repository
public interface MenuMapper {
    public List<Menu> getMenusByUserId(Long uid);

    List<Menu> getAllMenusWithRole();

    List<Menu> getAllMenusLikeTree();

    List<Integer> getMIdsByRId(Integer rid);
}
