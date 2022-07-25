package com.lx.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * 
 * @author Java2108
 * @email java2108@qf.com
 * @date 2021-12-09 15:47:47
 */
@Data
@TableName("invitation_data")
public class InvitationDataEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;
	/**
	 * 
	 */
	private Integer tid;
	/**
	 * 
	 */
	private Long pv; // 浏览量
	/**
	 * 
	 */
	private Long likes; // 点赞
	/**
	 * 
	 */
	private Long comments; // 评论
	/**
	 * 
	 */
	private Long collect; // 收藏

}
