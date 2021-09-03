package com.lc.clock.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankVO {
    private Map<String,BigDecimal> day;

    private BigDecimal allTime;

    private BigDecimal finishTime;

    private String username;

    private String nickname;

}
