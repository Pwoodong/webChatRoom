package com.jiu.webchat.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Package com.jiu.webchat.entity
 * ClassName SysUserEntity.java
 * Description 用户实体类
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-04 14:06
 **/
@Data
public class SysUserEntity implements Serializable {
    private Integer id;
    private String code;
    private String name;
    private String status;
    private String createTime;
}
