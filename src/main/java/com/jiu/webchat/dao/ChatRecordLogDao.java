package com.jiu.webchat.dao;

import com.jiu.webchat.entity.ChatRecordLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Package com.jiu.webchat.dao
 * ClassName ChatRecordLogDao.java
 * Description  用户聊天记录数据接口
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-04 11:50
 **/
@Component
public interface ChatRecordLogDao {

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
