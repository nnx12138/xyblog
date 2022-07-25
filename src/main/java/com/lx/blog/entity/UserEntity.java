package com.lx.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * 
 * @author Java2108
 * @email java2108@qf.com
 * @date 2021-12-09 15:47:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user")
public class UserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 
	 */
	private String username;
	/**
	 * 
	 */
	private String pwd;
	/**
	 * 
	 */
	private Integer sex;
	/**
	 * 
	 */
	private String mobile;
	/**
	 * 
	 */
	private String email;
	/**
	 * 
	 */
	private Integer statu; // 0:未激活，1:已激活，2:锁定，3：冻结
	/**
	 * 头像
	 */
	private String headUrl;
	/**
	 * 
	 */
	private String activateCode;
	/**
	 * 
	 */
	private Integer score;
	/**
	 * 
	 */
	private Date createTime;

	public UserEntity(Integer id, String headUrl) {
		this.id = id;
		this.headUrl = headUrl;
	}
}
