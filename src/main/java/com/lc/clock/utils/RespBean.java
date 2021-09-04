package com.lc.clock.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-30  9:50 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */
@Data
@AllArgsConstructor
public class RespBean<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer status;
    private String msg;
    private Object data;
    private long timestamp;

    public RespBean (){
        this.timestamp = System.currentTimeMillis();
    }


    public static <T> RespBean<T> ok(String msg) {
        RespBean<T> respBean = new RespBean<>();
        respBean.setStatus(0);
        respBean.setMsg(msg);
        respBean.setData(null);
        return respBean;
    }

    public static <T> RespBean<T> ok(String msg, Object obj) {
        RespBean<T> respBean = new RespBean<>();
        respBean.setStatus(0);
        respBean.setMsg(msg);
        respBean.setData(obj);
        return respBean;
    }

    public static <T> RespBean<T> error(String msg) {
        RespBean<T> respBean = new RespBean<>();
        respBean.setStatus(1);
        respBean.setMsg(msg);
        respBean.setData(null);
        return respBean;
    }

    public static <T> RespBean<T> error(String msg, Object obj) {
        RespBean<T> respBean = new RespBean<>();
        respBean.setStatus(1);
        respBean.setMsg(msg);
        respBean.setData(obj);
        return respBean;
    }

    public static <T> RespBean<T> error(Integer code, String msg) {
        RespBean<T> respBean = new RespBean<>();
        respBean.setStatus(code);
        respBean.setMsg(msg);
        respBean.setData(null);
        return respBean;
    }

}

