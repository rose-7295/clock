package com.lc.clock.controller.config;

import com.lc.clock.pojo.Menu;
import com.lc.clock.service.impl.MenuServiceImpl;
import com.lc.clock.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.SocketException;
import java.util.List;

/**
 * @author 柴柴快乐每一天
 * @create 2021-09-01  3:06 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */

@RestController
@RequestMapping("/config")
public class SystemConfigController {
    @Autowired
    MenuServiceImpl menuService;

    @GetMapping("/menu")
    public RespBean<List<Menu>> getMenuByUserId(HttpServletRequest request) throws SocketException {

        List<Menu> menus = menuService.getMenusByHrId();
        if (menus != null) {
            return RespBean.ok(null, menus);
        }

        return RespBean.error("菜单加载失败!");
    }
}
