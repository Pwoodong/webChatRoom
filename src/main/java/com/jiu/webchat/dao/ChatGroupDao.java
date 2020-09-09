package com.jiu.webchat.dao;

import com.jiu.webchat.entity.ChatGroupEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Package com.jiu.webchat.dao
 * ClassName ChatGroupDao.java
 * Description 群组数据接口
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-03 10:54
 **/
@Component
public interface ChatGroupDao {

    /**
     * 查询
     * @param
     * @return
     **/
    void selectById();

    /**
     * 查询群聊-根据用户ID
     * @param   userId      用户ID
     * @return  List
     **/
    List<ChatGroupEntity> selectGroupByUserId(String userId);

    /**
     * 删除
     * @param
     * @return
     **/
    int delete(String id);

    /**
     * 新增
     * @param
     * @return
     **/
    int insert(ChatGroupEntity record);

    /**
     * 修改
     * @param
     * @return
     **/
    int update(ChatGroupEntity record);

}
