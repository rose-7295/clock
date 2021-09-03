package com.lc.clock.config;

import com.lc.clock.pojo.Menu;
import com.lc.clock.pojo.Role;
import com.lc.clock.service.impl.MenuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-30  8:56 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */
@Component
public class CustomFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    MenuServiceImpl menuService;

    AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 根据用户请求的地址，分析出这个地址需要哪些角色才能访问
     * @param o
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        String requestUrl = ((FilterInvocation) o).getRequestUrl();
        List<Menu> menus = menuService.getAllMenusWithRole();
//        System.out.println(menus.toString());
        for (Menu menu : menus) {
            if (antPathMatcher.match(menu.getUrl(), requestUrl)){
                List<Role> roles = menu.getRoles();
//                System.out.println(roles.toString());
                String[] arr = new String[roles.size()];
                for (int i = 0; i < roles.size(); i++) {
                    arr[i] = roles.get(i).getName();
                }
                return SecurityConfig.createList(arr);
            }
        }
        return SecurityConfig.createList("ROLE_LOGIN");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}

