package com.lx.blog.vo;

import lombok.Data;

import java.util.Date;

@Data
public class FollowerVo {

    private Integer userId;

    private String username;

    private Date createTime;

    private String headUrl;
}
