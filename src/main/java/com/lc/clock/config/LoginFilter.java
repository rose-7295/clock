package com.lc.clock.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lc.clock.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-30  8:58 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    @Autowired
    SessionRegistry sessionRegistry;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        if (!"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String verify_code = (String) request.getSession().getAttribute("verify_code");

        // 传来key-value则调用security原生检验，否则调用自定义的重写方法
        if (request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE) || request.getContentType().contains(MediaType.APPLICATION_JSON_UTF8_VALUE)) {
//            用map接收前端传来的json对象
            Map<String, String> loginData = new HashMap<>();

            try {
                loginData = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                String code = (String) loginData.get("code");
                checkCode(response, code, verify_code);
            }

            String username = loginData.get(getUsernameParameter());
            username = username != null ? username : "";
            username = username.trim();
            String password = loginData.get(getPasswordParameter());
            password = password != null ? password : "";

            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);
            User principal = new User();
            principal.setUsername(username);
            sessionRegistry.registerNewSession(request.getSession(true).getId(), principal);
            return this.getAuthenticationManager().authenticate(authRequest);
        } else {
            checkCode(response, request.getParameter("code"), verify_code);
            return super.attemptAuthentication(request, response);
        }
    }

    private void checkCode(HttpServletResponse response, String code, String verify_code) {
        if (code == null || verify_code == null || "".equals(code) || !verify_code.equalsIgnoreCase(code)) {
            //验证码不正确
            throw new AuthenticationServiceException("验证码不正确");
        }
    }
}

