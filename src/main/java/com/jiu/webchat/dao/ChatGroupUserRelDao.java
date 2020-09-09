package com.jiu.webchat.dao;

import com.jiu.webchat.entity.ChatGroupUserRelEntity;
import org.springframework.stereotype.Component;

/**
 * Package com.jiu.webchat.dao
 * ClassName ChatGroupUserRelDao.java
 * Description 群组用户关系数据接口
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-03 11:07
 **/
@Component
public interface ChatGroupUserRelDao {

    /**
     * 查询
     * @param
     * @return
     **/
    void selectById();

    /**
     * 删除
     * @param
     * @return
     **/
    int delete(String id);

    /**
     * 批量新增
     * @param
     * @return
     **/
    int insertBatch(ChatGroupUserRelEntity record);

    /**
     * 新增
     * @param
     * @return
     **/
    int insert(ChatGroupUserRelEntity record);

    /**
     * 修改
     * @param
     * @return
     **/
    int update(ChatGroupUserRelEntity record);

}
