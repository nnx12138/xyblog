package com.lx.blog.inteceprot;

import com.lx.blog.common.constants.UserConstants;
import com.lx.blog.common.help.UserHelp;
import com.lx.blog.common.utils.CookieUtils;
import com.lx.blog.vo.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 如果用户登录了，而且Token校验成功，就把当前登录的用户放入到userHelp中。
 */
@Component
@Slf4j
public class LoginUserInterceprot implements HandlerInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserHelp userHelp;

    // controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1.从Cookie中获取Token
        String token = CookieUtils.getValue(request);

        // 2.校验Token(查询redis)
        if (!ObjectUtils.isEmpty(token)) {
            UserToken userToken = (UserToken) redisTemplate.opsForValue().get(String.format(UserConstants.USER_LOGIN_TOKEN, token));
            //  1.判断userToken是否为空，
            //  2:判断Token是否过期(Token的过期时间必须是当前时间之后)
            if (userToken != null && userToken.getTtl().after(new Date())) {
                // 放入到ThreadLocal中
                userHelp.set(userToken);
            } else {
                log.debug("token过期或者不合法:{}" + token);
                response.sendRedirect(request.getContextPath() + "/user/login");
            }
        }
        return true;
    }

    // Controller执行之后，视图解析之后，最后的方法
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        userHelp.remove();
    }

    // 视图解析之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 把当前登录的用户放到ModelMap中
        UserToken userToken = userHelp.get();
        if (userToken != null) {
            if (modelAndView != null) {
                modelAndView.addObject("loginUser", userToken);
            }
        }
    }
}
