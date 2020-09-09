package com.jiu.webchat.service;

import com.jiu.webchat.entity.ChatGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * Package com.jiu.webchat.service
 * ClassName ChatGroupService.java
 * Description 群组业务接口
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-03 11:08
 **/
public interface ChatGroupService {

    /**
     * 添加群聊信息
     * @param     userList      用户信息
     * @return    void
     **/
    void save(List<Map<String,Object>> userList);

    /**
     * 查询群聊-根据用户ID
     * @param   userId      用户ID
     * @return  List
     **/
    List<ChatGroupEntity> selectGroupByUserId(String userId);

}
