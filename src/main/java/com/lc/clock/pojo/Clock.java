package com.lc.clock.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Clock {
    private Long id;

    private String nickname;

    private Date startTime;

    private Date endTime;

    private BigDecimal clockTime;

    private BigDecimal monTime;

    private BigDecimal tueTime;

    private BigDecimal wedTime;

    private BigDecimal thuTime;

    private BigDecimal friTime;

    private BigDecimal satTime;

    private BigDecimal sunTime;
}