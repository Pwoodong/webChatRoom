package com.jiu.webchat.dao;

import com.jiu.webchat.entity.SysUserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Package com.jiu.webchat.dao
 * ClassName SysUserDao.java
 * Description 用户数据层接口
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-09 11:32
 **/
@Component
public interface SysUserDao {

    /**
     * 查询列表
     * @param       sysUserEntity       入参
     * @return      List
     **/
    List<SysUserEntity> selectList(SysUserEntity sysUserEntity);

}
