package com.linqibin.mall.member.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 *
 *
 * @author hugh
 * @email linqibin0826@gmail.com
 * @date 2021-01-10 20:03:40
 */
@Data
@TableName("undo_log")
public class UndoLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Long id;
	/**
	 *
	 */
	private Long branchId;
	/**
	 *
	 */
	private String xid;
	/**
	 *
	 */
	private String context;
	/**
	 *
	 */
	private byte[] rollbackInfo;
	/**
	 *
	 */
	private Integer logStatus;
	/**
	 *
	 */
	private Date logCreated;
	/**
	 *
	 */
	private Date logModified;
	/**
	 *
	 */
	private String ext;

}
