package com.lx.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by macro on 2021/12/15.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassVo {
//修改密码实体类
    private String oldPassword;

    private String newPassword;
}
