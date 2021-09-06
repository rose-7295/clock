package com.lc.clock.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lc.clock.pojo.User;
import com.lc.clock.service.UserService;
import com.lc.clock.service.impl.UserServiceImpl;
import com.lc.clock.utils.RespBean;
import com.lc.clock.utils.ResultVOUtil;
import com.lc.clock.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-30  8:52 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserServiceImpl userService;

    @Autowired
    CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;
    @Autowired
    CustomUrlDecisionManager customUrlDecisionManager;


    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    // 解决登录页面被拦截，跳出死循环
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/login","/regis", "/verifyCode", "/css/**", "/js/**", "/index.html", "/img/**", "/fonts/**", "/favicon.ico");
    }

    @Bean
    LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationSuccessHandler((request, response, authentication) -> {
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
            User user = (User) authentication.getPrincipal();
            // 这样前端就看不到密码了
            user.setPassword(null);
//            Integer posId = user.getPosId();

            RespBean ok = RespBean.ok("登录成功啦", user);
//            if (posId == 58) {
//                ok.setMsg("登录成功啦，今天该你扫地噢");
//            }
            String s = new ObjectMapper().writeValueAsString(ok);
            out.write(s);
            out.flush();
            out.close();
        });
        loginFilter.setAuthenticationFailureHandler((request, response, exception)->{
            response.setContentType("application/json;charset=utf-8");
            PrintWriter out = response.getWriter();
//                        Hr hr = (Hr) authentication.getPrincipal();  登录失败拿不到
            RespBean error = RespBean.error("登录失败!");
            if (exception instanceof LockedException) {
                error.setMsg("账户被锁定，请联系管理员！");
            } else if (exception instanceof CredentialsExpiredException) {
                error.setMsg("密码过期，请联系管理员！");
            } else if (exception instanceof AccountExpiredException) {
                error.setMsg("账户过期，请联系管理员！");
            } else if (exception instanceof DisabledException) {
                error.setMsg("账户被禁用，请联系管理员！");
            } else if (exception instanceof BadCredentialsException) {
                error.setMsg("用户名或密码输入错误，请重新输入！");
            } else if (exception instanceof AuthenticationServiceException) {
                error.setMsg(exception.getMessage());
            }
            String s = new ObjectMapper().writeValueAsString(error);
            out.write(s);
            out.flush();
            out.close();
        });

        // 在LoginFilter中用到了
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        loginFilter.setFilterProcessesUrl("/doLogin");

        // 多端登录踢下线
        ConcurrentSessionControlAuthenticationStrategy sessionStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        sessionStrategy.setMaximumSessions(1);
        loginFilter.setSessionAuthenticationStrategy(sessionStrategy);


        return loginFilter;

    }

    // 多端登录踢下线
    @Bean
    SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 在用户名密码之前校验,用自定义loginFilter替代了
//        http.addFilterBefore(verificationCodeFilter, UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests()
//                .anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                        o.setAccessDecisionManager(customUrlDecisionManager);
                        o.setSecurityMetadataSource(customFilterInvocationSecurityMetadataSource);
                        return o;
                    }
                })
                .and()
                .logout()
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter writer = resp.getWriter();
                        String s = new ObjectMapper().writeValueAsString(RespBean.error("注销成功！"));
                        writer.write(s);
                        writer.flush();
                        writer.close();
                    }
                })
                .permitAll()
                .and()
                .addFilterAt(new ConcurrentSessionFilter(sessionRegistry(), event -> {
                    HttpServletResponse resp = event.getResponse();
                    resp.setContentType("application/json;charset=utf-8");
                    resp.setStatus(401);
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(RespBean.error("您已在另一台设备登录，本次登录已下线!")));
                    out.flush();
                    out.close();
                }), ConcurrentSessionFilter.class)
                .csrf().disable()
                .exceptionHandling()
                // 没有认证时，在这里处理结果，不要重定向
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        resp.setStatus(401);
                        PrintWriter out = resp.getWriter();
                        RespBean error = RespBean.error("访问失败！");
                        if (e instanceof InsufficientAuthenticationException) {
                            error.setMsg("请求失败，请联系管理员！");
                        }
                        String s = new ObjectMapper().writeValueAsString(error);
                        out.write(s);
                        out.flush();
                        out.close();
                    }
                });

//        // 多端登录踢下线
//        http.addFilterAt(new ConcurrentSessionFilter(sessionRegistry(), event -> {
//            HttpServletResponse resp = event.getResponse();
//            resp.setContentType("application/json;charset=utf-8");
//            resp.setStatus(401);
//            PrintWriter out = resp.getWriter();
//            out.write(new ObjectMapper().writeValueAsString(RespBean.error("您已在另一台设备登录，本次登录已下线!")));
//            out.flush();
//            out.close();
//        }), ConcurrentSessionFilter.class);
        // 把自定义的登录过滤器加入到这里面来
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
