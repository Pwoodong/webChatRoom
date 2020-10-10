package com.jiu.webchat.service.impl;

import com.jiu.webchat.dao.SysUserDao;
import com.jiu.webchat.dto.SessionUserDto;
import com.jiu.webchat.entity.ChatGroupEntity;
import com.jiu.webchat.entity.SysUserEntity;
import com.jiu.webchat.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Package com.jiu.webchat.service.impl
 * ClassName SysUserServiceImpl.java
 * Description 用户业务接口实现
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-09 11:37
 **/
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserDao sysUserDao;

    @Override
    public List<SysUserEntity> selectList(SysUserEntity sysUserEntity){
        return sysUserDao.selectList(sysUserEntity);
    }

    @Override
    public List<SessionUserDto> selectSessionUserList(SysUserEntity sysUserEntity) {
        List<SessionUserDto> list = new ArrayList<>();
        List<SysUserEntity> sysUserList = sysUserDao.selectList(sysUserEntity);
        for(SysUserEntity sysUser:sysUserList){
            SessionUserDto sessionUserDto = new SessionUserDto();
            sessionUserDto.setUsername(sysUser.getName());
            sessionUserDto.setStatus("离线");
            list.add(sessionUserDto);
        }
        Collections.reverse(list);
        return list;
    }

    @Override
    public List<SysUserEntity> selectUserList(ChatGroupEntity chatGroupEntity) {
        return sysUserDao.selectUserList(chatGroupEntity);
    }

}
