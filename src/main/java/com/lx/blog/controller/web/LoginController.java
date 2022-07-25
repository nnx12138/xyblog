package com.lx.blog.controller.web;

import com.lx.blog.common.constants.UserConstants;
import com.lx.blog.entity.UserEntity;
import com.lx.blog.common.utils.R;
import com.lx.blog.service.UserService;
import com.lx.blog.vo.UserToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author 牛牛昕
 * @date 2021-12-09 15:47:47
 */
@Controller
@RequestMapping("/user") //user/login
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public R login(@RequestBody UserEntity userEntity, HttpServletResponse response) {
        log.debug("1、用户正在完成登录。。。userEntity:{}", userEntity);
        // 1.实现登录
        UserToken userToken = userService.login(userEntity.getUsername(), userEntity.getPwd());
        log.debug("2、userToken生成成功了:userToken:{}", userToken);

        // 2.把token放到用户的浏览器中(现在是Web端可以写入Cookie中，如果是非Web端可以把Token直接返回给用户，由用户来决定Token的储存位置)
        Cookie cookie = new Cookie(UserConstants.TOKEN_COOKIE_KEY, userToken.getToken());
        cookie.setPath("/");
        cookie.setMaxAge(UserConstants.USER_DEFAULE_TIMEOUT); // 10天

        response.addCookie(cookie);
        log.debug("3、写入Cookie成功:userToken:{}", cookie);
        return R.ok();
    }

    @GetMapping("/activateUser/{code}")
    @ResponseBody
    public String activateUser(@PathVariable String code) {

        if (!ObjectUtils.isEmpty(code)) {
            userService.activateUser(code);
        }
        return "<html>账号已经激活，点击<a href ='http://localhost:8001/site/login.html'>这里</a>进行登录</html>";
    }

    @PostMapping("/register")
    @ResponseBody
    // public R register(@RequestBody UserEntity userEntity) {
    public R register(@RequestParam HashMap<String, Object> params) {

        //  log.debug("userEntity:{}", userEntity);

        // 1.注册用户
        UserEntity userEntity = mapToBean(params, UserEntity.class);
        userService.register(userEntity);

        return R.ok();
    }


    @GetMapping("/logout")
    @ResponseBody
    public R logout(@CookieValue(name = UserConstants.TOKEN_COOKIE_KEY, defaultValue = "") String token, HttpServletResponse response) {

        // 删除服务端的token
        userService.logout(token);

        // 把客户端的cookie也要删除
        Cookie cookie = new Cookie(UserConstants.TOKEN_COOKIE_KEY, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return R.ok();
    }

    @RequestMapping("/adminlogin")
    public String adminlogin() {
        return "admin/login";
    }

    public <T> T mapToBean(HashMap<String, Object> map, Class<T> c) {
        //  <T> T     这里不确定返回值类型，写一个泛型类型，作为返回值

        //  HashMap<String, Object> map,Class<T> c
        //  这里在方法中传两个参数，一个是map集合，另一个是实体类，
        //  但是实体类不确定，会一直变动，所以传递一个Class类作为参数类型，可以拿到所有的实体类

        try {
            T t = c.newInstance();      //拿到实体类对象
            //1，拆开map,给对象t的属性赋值
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                Field f = c.getDeclaredField(entry.getKey());
                f.setAccessible(true);  //设置修改权限，因为我们采用的封装private，不修改权限的话，就不能修改实体类中的属性值
                f.set(t, entry.getValue());  //传递参数，第一个为实体类对象，第二个为map集合中的value值
            }
            //2，构建一个实体对象并返回
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("出错啦！"); //将异常放大，可不用写返回值
        }
    }
}