package com.jiu.webchat.service.impl;

import com.jiu.webchat.dao.ChatGroupDao;
import com.jiu.webchat.dao.ChatGroupUserRelDao;
import com.jiu.webchat.entity.ChatGroupEntity;
import com.jiu.webchat.entity.ChatGroupUserRelEntity;
import com.jiu.webchat.service.ChatGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Package com.jiu.webchat.service.impl
 * ClassName ChatGroupServiceImpl.java
 * Description 群组业务接口实现
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-03 11:09
 **/
@Service
public class ChatGroupServiceImpl implements ChatGroupService {

    @Autowired
    private ChatGroupDao chatGroupDao;

    @Autowired
    private ChatGroupUserRelDao chatGroupUserRelDao;

    @Override
    @Transactional
    public void save(List<Map<String,Object>> userList) {
        ChatGroupEntity chatGroupEntity = new ChatGroupEntity();
        String groupName = "";
        for(Map<String,Object> map : userList){
            groupName += map.get("userName") + ",";
        }
        if(groupName.length() > 20){
            groupName = groupName.substring(0,20);
        }
        chatGroupEntity.setGroupName(groupName);
        int i = chatGroupDao.insert(chatGroupEntity);
        if(i > 0){
            List<String> userIdList = new ArrayList<>();
            for(Map<String,Object> map : userList){
                userIdList.add(map.get("userId").toString());
            }
            ChatGroupUserRelEntity chatGroupUserRelEntity = new ChatGroupUserRelEntity();
            chatGroupUserRelEntity.setGroupId(chatGroupEntity.getId());
            chatGroupUserRelEntity.setUserIdList(userIdList);
            chatGroupUserRelDao.insertBatch(chatGroupUserRelEntity);
        }
    }

    @Override
    public List<ChatGroupEntity> selectGroupByUserId(String userId) {
        return chatGroupDao.selectGroupByUserId(userId);
    }

}
