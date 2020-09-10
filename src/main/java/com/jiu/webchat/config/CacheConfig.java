package com.jiu.webchat.config;

import com.jiu.webchat.dto.SessionUserDto;
import com.jiu.webchat.entity.SysUserEntity;
import com.jiu.webchat.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

/**
 * Package com.jiu.webchat.config
 * ClassName CacheConfig.java
 * Description 缓存配置
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-10 9:03
 **/
@Component
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    public static List<SessionUserDto> userCache = new ArrayList<>();

    @Autowired
    private SysUserService sysUserService;

    @PostConstruct
    public void init() {
        logger.info("系统启动,初始化系统用户数据到缓存中.");
        SysUserEntity sysUserEntity = new SysUserEntity();
        List<SysUserEntity> userList = sysUserService.selectList(sysUserEntity);
        for (SysUserEntity entity : userList) {
            SessionUserDto userDto = new SessionUserDto();
            userDto.setId(entity.getId().toString());
            userDto.setUsername(entity.getName());
            userDto.setStatus("离线");
            userCache.add(userDto);
        }
    }

    @PreDestroy
    public void destroy() {
        logger.info("系统运行结束,用户缓存数据已被清除.");
    }
}
