package com.lc.clock.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class User {
    private Long id;

    private String nickname;

    private String username;

    private String password;

    private Integer grade;

    private Date createTime;

    private Date updateTime;

    private String role;

    private String avatar;

    private BigDecimal alltime;

    private Integer online;
}