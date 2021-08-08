package com.lc.clock.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class UserVO {
    private Long id;

    private String nickname;

    private String username;

    private String password;

    private Integer grade;

    private Integer time;

    private List<String> role;

    private String avatar;

    private Integer online;

    private BigDecimal alltime;
}
