package com.linqibin.mall.member.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ums_member_oauth")
public class MemberOauthEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private String uid;
    private String token;
    private LocalDateTime expiredDate;
    private Long memberId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleteFlag;
    private Integer version;
}
