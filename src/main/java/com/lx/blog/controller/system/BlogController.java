package com.lx.blog.controller.system;


import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.common.systemUtils.PageResult;
import com.lx.blog.common.utils.R;
import com.lx.blog.entity.InvitationEntity;
import com.lx.blog.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
@RequestMapping("/admin")
public class BlogController {
    @Autowired
    private InvitationService invitationService;

    @GetMapping("/blogs/list")
    @ResponseBody
    public R list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return null;
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        PageResult blogsPage = invitationService.getBlogsPage(pageUtil);
        return R.ok(blogsPage);
    }

    @GetMapping("/blogs")
    public String list(HttpServletRequest request) {
        request.setAttribute("path", "blogs");
        return "admin/blog";
    }

    @GetMapping("/blogs/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        request.setAttribute("categories", invitationService.getAllCategories());
        return "admin/edit";
    }

    @GetMapping("/blogs/edit/{blogId}")
    public String edit(HttpServletRequest request, @PathVariable("blogId") Long blogId) {
        request.setAttribute("path", "edit");
        InvitationEntity blog = invitationService.getBlogById(blogId);
        if (blog == null) {
            return "error/error_400";
        }
        request.setAttribute("blog", blog);
        request.setAttribute("categories", invitationService.getAllCategories());
        return "/admin/edit";
    }

    @PostMapping("/blogs/update")
    public String update(@RequestParam("blogId") Long blogId,
                         @RequestParam("blogName") String blogTitle,
                    @RequestParam("blogTags") String blogTags) {

        InvitationEntity entity = new InvitationEntity();
        int id = Math.toIntExact(blogId);
        entity.setId(id);
        entity.setTitle(blogTitle);
        entity.setContent(blogTags);
        boolean b = invitationService.updateById(entity);
        if (b) {
            return "redirect:/admin/blogs";
        } else {
            return null;
        }
    }
    @PostMapping("/blogs/delete")
    @ResponseBody
    public R delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return R.error("参数异常！");
        }
        if (invitationService.deleteBatch(ids)) {
            return R.ok();
        } else {
            return R.error("删除失败");
        }
    }


}
