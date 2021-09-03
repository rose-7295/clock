package com.lc.clock.controller;

import com.lc.clock.config.VerificationCode;
import com.lc.clock.pojo.User;
import com.lc.clock.service.impl.UserServiceImpl;
import com.lc.clock.utils.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-31  11:05 上午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */
@RestController
public class LoginAndRegisterController {

    @Autowired
    UserServiceImpl userService;

    @GetMapping("/login")
    public RespBean login() {
        return RespBean.error("尚未登录，请登录！");
    }

//    需要在SecurityConfig里配置，否则因为权限管理会被拦截

    @PostMapping("/regis")
    public RespBean<User> register(@RequestParam("nickname") String nickname,
                                   @RequestParam("username") String username,
                                   @RequestParam("grade") Integer grade,
                                   @RequestParam("password") String password) {
        if (userService.register(nickname, username, grade, password) == 1) {
            return RespBean.ok("注册成功");
        }
        return RespBean.error("注册失败!");
    }

    @GetMapping("/verifyCode")
    public void verifyCode(HttpSession session, HttpServletResponse resp) throws IOException {
        VerificationCode code = new VerificationCode();
        BufferedImage image = code.getImage();
        String text = code.getText();
        session.setAttribute("verify_code", text);
        VerificationCode.output(image, resp.getOutputStream());
    }
}

