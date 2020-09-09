package com.jiu.webchat.service;

import com.jiu.webchat.entity.ChatRecordLogEntity;

import java.util.List;

/**
 * Package com.jiu.webchat.service
 * ClassName ChatRecordLogService.java
 * Description 用户聊天记录接口
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-04 13:29
 **/
public interface ChatRecordLogService {

    /**
     * 查询列表
     * @param       chatRecordLogEntity       入参
     * @return      List
     **/
    List<ChatRecordLogEntity> selectByList(ChatRecordLogEntity chatRecordLogEntity);

    /**
     * 查询附件列表
     * @param       ids       入参
     * @return      List
     **/
    List<ChatRecordLogEntity> selectListByIdS(String ids);

    /**
     * 插入
     * @param       chatRecordLogEntity       入参
     * @return      void
     **/
    void insert(ChatRecordLogEntity chatRecordLogEntity);
}
