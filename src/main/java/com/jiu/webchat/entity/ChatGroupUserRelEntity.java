package com.jiu.webchat.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Package com.jiu.webchat.entity
 * ClassName ChatGroupUserRelEntity.java
 * Description 群组用户关系实体类
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-03 10:53
 **/
@Data
public class ChatGroupUserRelEntity implements Serializable {
    private Integer id;
    private Integer groupId;
    private String userId;
    private Date createTime;
    private String createBy;
    private Date updateTime;
    private String updateBy;
    private List<String> userIdList;
}
