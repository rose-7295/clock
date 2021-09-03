package com.lc.clock.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailVO {
    private String username;

    private String nickname;

    private Integer grade;

    private Integer online;

    private String avatar;

    private BigDecimal allTime;

    private BigDecimal finishTime;

    private Date startTime;

    private Date endTime;
}
