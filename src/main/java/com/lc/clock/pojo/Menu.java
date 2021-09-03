package com.lc.clock.pojo;

import lombok.Data;

import java.util.List;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-30  9:43 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */
@Data
public class Menu {
    private Integer id;

    private String url;

    private String key;

    private String component;

    private String title;

    private String icon;

    private Meta meta;
//    private Boolean keepAlive;
//
//    private Boolean requireAuth;

    private List<Meta> children;

    private Integer parentId;

    private Boolean enabled;

    // 该菜单哪些角色才能访问
    private List<Role> roles;


}
