package com.lc.clock.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class RankVO {
    private Map<String,BigDecimal> day;

    private BigDecimal allTime;

    private BigDecimal finishTime;

    private String username;

    private String nickname;

}
