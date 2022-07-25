package com.lx.blog.controller.web;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import com.lx.blog.common.help.UserHelp;
import com.lx.blog.common.constants.UserConstants;
import com.lx.blog.common.exception.BlogException;
import com.lx.blog.common.utils.PasswordUtils;
import com.lx.blog.common.utils.R;
import com.lx.blog.entity.UserEntity;
import com.lx.blog.service.UserService;
import com.lx.blog.vo.PassVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

;

/**
 * Created by macro on 2021/12/14.
 */
@CrossOrigin
@Controller
@RequestMapping("/setting")
@MultipartConfig(maxFileSize = 1024 * 1024 * 100, maxRequestSize = 1024 * 1024 * 100)//设置上传文件的最大值
public class SettingController {

    @Autowired
    private UserHelp userHelp;

    @Autowired
    private UserService userService;

//    E:\ideaworkspaces\2108\blog\src\main\resources\static\img\image\1.png
    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public void upLoad(@RequestParam("img") MultipartFile img, HttpServletResponse response) throws IOException {
        if (userHelp == null) {
            throw new BlogException("请先登录");
        }

//        System.out.println(System.getProperty("user.dir")); //E:\ideaworkspaces\2108\blog
        File directory = new File(System.getProperty("user.dir") + UserConstants.IMAGEPATH + UserConstants.HEADURL); // 找到项目最外层地址
        String reportPath = directory.getCanonicalPath();  //  E:\ideaworkspaces\2108\blog\src\main\resources\static\img\image
        String name = img.getOriginalFilename(); //获取文件名  8546691.jpg
//        System.out.println(name);
//        System.out.println(reportPath);

        String path = reportPath + "\\" + name;
        System.out.println(path);

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        img.transferTo(file);

        String headUrl = UserConstants.HEADURL + "/" + name;

        userService.updateById(new UserEntity(userHelp.get().getUid(), headUrl));
        response.sendRedirect("/site/login.html");
    }


    /**
     * 重置密码
     */
    @PostMapping("/updatePass")
    @ResponseBody
    public R updatePass(@RequestBody PassVo passVo) {
        Integer uid = userHelp.get().getUid();
        UserEntity user = userService.getById(uid);
        if (passVo != null) {
            if (!PasswordUtils.checkpw(passVo.getOldPassword(), user.getPwd())) {
                throw new BlogException("原密码输入有误");
            }
            String encode = PasswordUtils.encode(passVo.getNewPassword());
            UpdateWrapper<UserEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("pwd", user.getPwd()).set("pwd", encode);
            boolean update = userService.update(null, updateWrapper);
            if (update) {
                return R.ok("重置密码成功");
            }
        }
        return R.error("重置密码失败");
    }
}
