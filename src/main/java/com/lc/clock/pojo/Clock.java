package com.lc.clock.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class Clock {
    private BigDecimal zero = new BigDecimal(0);

    private Long id;

    private String username;

    private Date startTime;

    private Date endTime;

    private BigDecimal clockTime = zero;

    private BigDecimal monTime = zero;

    private BigDecimal tueTime = zero;

    private BigDecimal wedTime = zero;

    private BigDecimal thuTime = zero;

    private BigDecimal friTime = zero;

    private BigDecimal satTime = zero;

    private BigDecimal sunTime = zero;

    public Clock(String username) {
        this.username = username;
    }
}