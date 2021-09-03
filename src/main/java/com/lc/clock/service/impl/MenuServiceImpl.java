package com.lc.clock.service.impl;

import com.lc.clock.dao.MenuMapper;
import com.lc.clock.dao.MenuRoleMapper;
import com.lc.clock.pojo.Menu;
import com.lc.clock.pojo.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-30  9:53 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */
@Service
public class MenuServiceImpl {
    @Resource
    MenuMapper menuMapper;

    @Resource
    MenuRoleMapper menuRoleMapper;

    public List<Menu> getMenusByHrId() {
        // 返回的是一个对象
        return menuMapper.getMenusByUserId(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
    }

    /**
     * 返回所有的菜单项，用于展示页面
     * @return
     */
//    @Cacheable
    public List<Menu> getAllMenusWithRole() {
        return menuMapper.getAllMenusWithRole();
    }


    /**
     * 返回的是一个菜单相当于，内嵌多层子菜单
     * 不能更改成一个菜单，还是需要List，因为前端数据需要的是一个数组
     * @return
     */
    public List<Menu> getAllMenusLikeTree() {
        return menuMapper.getAllMenusLikeTree();
    }

    public List<Integer> getMIdsByRId(Integer rid) {
        return menuMapper.getMIdsByRId(rid);
    }

    /**
     * 先删除再插入
     * @param rid
     * @param mids
     * @return
     */
//    @Transactional
    public boolean updateMenuRole(Integer rid, Integer[] mids) {
        menuRoleMapper.deleteByRid(rid);
        if (mids == null || mids.length == 0) {
            return true;
        }
        Integer res = menuRoleMapper.insertRecord(rid, mids);
        return res == mids.length;
    }
}

