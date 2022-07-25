package com.lx.blog.controller.system;

import com.lx.blog.common.utils.R;
import com.lx.blog.entity.UserEntity;
import com.lx.blog.service.CommentService;
import com.lx.blog.service.InvitationService;
import com.lx.blog.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin")
@Slf4j
public class SystemUserController {

    @Autowired
    private UserService userService;
    @Autowired
    private InvitationService invitationService;
    @Autowired
    private CommentService commentService;

    @PostMapping(value = "/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,HttpServletRequest request) {
        UserEntity userEntity = userService.adminlogin(userName, password);
        if (userEntity != null) {
            request.setAttribute("path","index");
            return "redirect:/admin/index";
        } else {
            return "admin/login";
        }
    }


    @GetMapping({"", "/", "/index", "/index.html"})
    public String index(HttpServletRequest request) {
        request.setAttribute("path", "index");
        request.setAttribute("categoryCount", userService.count());
        request.setAttribute("blogCount",invitationService.count());
        request.setAttribute("commentCount",commentService.count());
        return "admin/index";
    }
    
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        List<UserEntity> list = userService.list();
        R ok = R.ok();
        R data = ok.put("data", list);
        System.out.println("OK:" + ok);
        System.out.println("data:" + data.get("code") + "," + data.get("data"));
        System.out.println(ok == data);
        return data;
    }



}
