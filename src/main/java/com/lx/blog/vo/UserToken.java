package com.lx.blog.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

// 用户登录的凭证
@Data
public class UserToken implements Serializable {

    private Integer uid; // 用户ID

    private String uname; // 用户的名称

    private Date ttl; // 过期时间

    private Date creatTime; // 登录的时间

    private String token; // 令牌，需要把这个令牌颁发给用户

    private String headerUrl; // 用户的头像
}
