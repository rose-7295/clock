package com.lc.clock.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class UserVO {
    private String nickname;

    private String username;

    private Integer grade;

    private List<String> role;

    private Integer online;

    private BigDecimal allTime;

    private BigDecimal finishTime;
}
