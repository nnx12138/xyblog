package com.lx.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
@TableName("t_invitation")
public class InvitationEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 
	 */
	private String title;
	/**
	 * 
	 */
	private String content;
	/**
	 * 
	 */
	private Integer statu;
	/**
	 * 
	 */
	private Integer uid;
	/**
	 * 
	 */
	private Date createTime;

}
