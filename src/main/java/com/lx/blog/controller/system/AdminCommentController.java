package com.lx.blog.controller.system;

import com.lx.blog.common.systemUtils.PageQueryUtil;
import com.lx.blog.common.systemUtils.PageResult;
import com.lx.blog.common.utils.R;
import com.lx.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 牛牛昕
 * @create 2022-02-17-14:44
 */
@Controller
@RequestMapping("/admin")
public class AdminCommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/comments/list")
    @ResponseBody
    public R list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return R.error("参数异常");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        PageResult page = commentService.getCommentsPage(pageUtil);
        return R.ok(page);
    }
    @GetMapping("/comments")
    public String comlist(HttpServletRequest request) {
        request.setAttribute("path", "comments");
        return "admin/comment";
    }

    @PostMapping("/comments/delete")
    @ResponseBody
    public R delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return R.error("参数异常！");
        }
        if (commentService.deleteBatch(ids)) {
            return R.ok();
        } else {
            return R.error("刪除失败");
        }
    }

}
