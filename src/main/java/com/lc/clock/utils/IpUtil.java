package com.lc.clock.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 柴柴快乐每一天
 * @create 2021-09-02  8:09 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */
public class IpUtil {
    private final static String IP = "192.168.31";

    public static Boolean isLecIP(HttpServletRequest request)
    {

        String ip = request.getHeader("LOCAL-IP");
        String substring = ip.substring(0, 10);
        return IP.equals(substring);
    }

}
