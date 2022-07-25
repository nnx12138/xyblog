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
 * @author 2108
 * @email 2108@gmail.com
 * @date 2021-12-12 14:50:37
 */
@Data
@TableName("t_comment")
public class CommentEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId(type = IdType.AUTO)
	private Integer id;

	private Integer uid;
	/**
	 *
	 * 1:评论帖子
	 * 2:评论回复
	 */
	private Integer entityType;
	private Integer entityId; // 帖子id/评论ID
	private Integer targetId;

	private String content;
	private Integer statu;
	private Date createTime;

}
