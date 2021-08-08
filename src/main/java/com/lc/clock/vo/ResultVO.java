package com.lc.clock.vo;

/*从http请求返回的最外层对象*/

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> {
    /*错误码*/
    private Integer code;

    /*提示信息*/
    private String msg;

    /*具体内容*/
    private T data;
}
