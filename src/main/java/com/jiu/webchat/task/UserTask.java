package com.jiu.webchat.task;

import com.jiu.webchat.dto.SessionUserDto;
import com.jiu.webchat.entity.SysUserEntity;
import com.jiu.webchat.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static com.jiu.webchat.config.CacheConfig.userCache;

/**
 * Package com.jiu.webchat.task
 * ClassName UserTask.java
 * Description 用户任务
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-15 13:54
 **/
@Component
public class UserTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(UserTask.class);

    @Autowired
    private SysUserService sysUserService;

    @Override
    public void run() {
        logger.info("开始用户任务." + new Date());
        userCache = null;
        SysUserEntity sysUserEntity = new SysUserEntity();
        List<SysUserEntity> userList = sysUserService.selectList(sysUserEntity);
        for (SysUserEntity entity : userList) {
            SessionUserDto userDto = new SessionUserDto();
            userDto.setId(entity.getId().toString());
            userDto.setUsername(entity.getName());
            userDto.setStatus("离线");
            userCache.add(userDto);
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("用户任务结束." + new Date());
    }

}
