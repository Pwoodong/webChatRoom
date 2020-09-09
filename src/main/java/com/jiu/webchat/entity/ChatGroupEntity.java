package com.jiu.webchat.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Package com.jiu.webchat.entity
 * ClassName ChatGroupEntity.java
 * Description 群组实体类
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-03 10:52
 **/
@Data
public class ChatGroupEntity implements Serializable {
    private Integer id;
    private String groupCode;
    private String groupName;
    private String status;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
}
