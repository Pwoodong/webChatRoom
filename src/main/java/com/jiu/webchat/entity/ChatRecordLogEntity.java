package com.jiu.webchat.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Package com.jiu.webchat.entity
 * ClassName ChatRecordLogEntity.java
 * Description 用户聊天记录实体
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-04 11:47
 **/
@Data
public class ChatRecordLogEntity implements Serializable {
    private String id;
    private String type;
    private String name;
    private String loginName;
    private String text;
    private String groupId;
    private String userId;
    private String filePath;
    private String district;
    private String createTime;
}
