package com.jiu.webchat.service.impl;

import com.jiu.webchat.dao.ChatRecordLogDao;
import com.jiu.webchat.entity.ChatRecordLogEntity;
import com.jiu.webchat.service.ChatRecordLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Package com.jiu.webchat.service.impl
 * ClassName ChatRecordLogServiceImpl.java
 * Description 用户聊天记录接口实现
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-04 13:29
 **/
@Service
public class ChatRecordLogServiceImpl implements ChatRecordLogService {

    @Autowired
    private ChatRecordLogDao chatRecordLogDao;

    @Override
    public List<ChatRecordLogEntity> selectByList(ChatRecordLogEntity chatRecordLogEntity) {
        List<ChatRecordLogEntity> list = chatRecordLogDao.selectByList(chatRecordLogEntity);
        List<ChatRecordLogEntity> result = new ArrayList<>();
        if(list != null && list.size() > 0){
            for(ChatRecordLogEntity chatRecordLog:list){
                ChatRecordLogEntity entity = new ChatRecordLogEntity();
                entity.setId(chatRecordLog.getId());
                entity.setName(chatRecordLog.getCreateTime() + "  " + chatRecordLog.getName());
                if("2".equals(chatRecordLog.getType())){
                    entity.setText(chatRecordLog.getText());
                    entity.setLoginName(chatRecordLog.getCreateTime() + "  " + chatRecordLog.getName() + "说：" + chatRecordLog.getText());
                } else {
                    entity.setText(chatRecordLog.getCreateTime() + "  " + chatRecordLog.getName() + "说：" + chatRecordLog.getText());
                }
                entity.setType(chatRecordLog.getType());
                entity.setFilePath(chatRecordLog.getFilePath());
                result.add(entity);
            }
        }
        Collections.reverse(result);
        return result;
    }

    @Override
    public List<ChatRecordLogEntity> selectListByIdS(String ids) {
        return chatRecordLogDao.selectListByIdS(ids);
    }

    @Override
    public void insert(ChatRecordLogEntity chatRecordLogEntity) {
        chatRecordLogDao.insert(chatRecordLogEntity);
    }

}
