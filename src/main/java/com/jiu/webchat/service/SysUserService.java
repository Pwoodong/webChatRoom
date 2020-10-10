package com.jiu.webchat.service;

import com.jiu.webchat.dto.SessionUserDto;
import com.jiu.webchat.entity.ChatGroupEntity;
import com.jiu.webchat.entity.SysUserEntity;

import java.util.List;

/**
 * Package com.jiu.webchat.service
 * ClassName SysUserService.java
 * Description 用户业务接口
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-09 11:37
 **/
public interface SysUserService {

    /**
     * 查询列表
     * @param       sysUserEntity       入参
     * @return      List
     **/
    List<SysUserEntity> selectList(SysUserEntity sysUserEntity);

    /**
     * 查询列表
     * @param       chatGroupEntity       入参
     * @return      List
     **/
    List<SysUserEntity> selectUserList(ChatGroupEntity chatGroupEntity);

    /**
     * 查询列表
     * @param       sysUserEntity       入参
     * @return      List
     **/
    List<SessionUserDto> selectSessionUserList(SysUserEntity sysUserEntity);

}
