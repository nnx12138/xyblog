package com.lx.blog.controller.system;

import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.common.systemUtils.PageResult;
import com.lx.blog.common.utils.R;
import com.lx.blog.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 牛牛昕
 * @create 2022-02-17-16:26
 */
@Controller
@RequestMapping("/admin")
public class UsersController {
    @Resource
    private UserService userService;

    @GetMapping("/categories")
    public String categoryPage(HttpServletRequest request) {
        request.setAttribute("path", "categories");
        return "admin/category";
    }
    /**
     * 用户列表
     */
    @RequestMapping(value = "/categories/list", method = RequestMethod.GET)
    @ResponseBody
    public R list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return R.error("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        PageResult page = userService.getBlogCategoryPage(pageUtil);
        return R.ok(page);
    }
    /**
     * 用户修改
     */
    @RequestMapping(value = "/categories/update", method = RequestMethod.POST)
    @ResponseBody
    public R update(@RequestParam("categoryId") Integer categoryId,
                         @RequestParam("categoryName") String categoryName,
                         @RequestParam("categoryIcon") String categoryIcon) {
        if (StringUtils.isEmpty(categoryName)) {
            return R.error("请输入用户名称！");
        }
        if (StringUtils.isEmpty(categoryIcon)) {
            return R.error("请选择用户图标！");
        }
        if (userService.updateCategory(categoryId, categoryName, categoryIcon)) {
            return R.ok();
        } else {
            return R.error("用户名称重复");
        }
    }


    /**
     * 用户删除
     */
    @RequestMapping(value = "/categories/delete", method = RequestMethod.POST)
    @ResponseBody
    public R delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return R.error("参数异常！");
        }
        if (userService.deleteBatch(ids)) {
            return R.ok();
        } else {
            return R.error("删除失败");
        }
    }


}
