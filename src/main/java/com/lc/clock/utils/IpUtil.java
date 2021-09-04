package com.lc.clock.utils;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 柴柴快乐每一天
 * @create 2021-09-02  8:09 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */
public class IpUtil {
    private final static String IP = "192.168.31";



    public static Integer isLecIP(HttpServletRequest request)
    {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm");
        String format = simpleDateFormat.format(new Date());
        String concatIp = IP + format;
        System.out.println(concatIp);
        String md5Ip= DigestUtils.md5Hex(concatIp);
        System.out.println(md5Ip);



        String ip = request.getHeader("LOCAL-IP");
        if ("offRTC".equals(ip)) {
            // 没有打开浏览器权限
            return 2;
        }
//        String substring = ip.substring(0, 10);
        if (md5Ip.equals(ip)) {
            return 1;
        }
        // 没有连接指定网络
        return 0;
    }

}
