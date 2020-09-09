package com.jiu.webchat.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jiu.webchat.entity.ChatGroupEntity;
import com.jiu.webchat.entity.SysUserEntity;
import com.jiu.webchat.service.ChatGroupService;
import com.jiu.webchat.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Package com.jiu.webchat.controller
 * ClassName ChatGroupController.java
 * Description 聊天群组接口
 *
 * @author Liaoyj
 * @version V1.0
 * @date 2020-09-03 11:15
 **/
@Controller
@RequestMapping(value = "chatGroup")
public class ChatGroupController {

    private static final Logger logger = LoggerFactory.getLogger(ChatGroupController.class);

    @Autowired
    private ChatGroupService chatGroupService;

    @Autowired
    private SysUserService sysUserService;

    @ResponseBody
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@RequestParam("userInfo") String userInfo) {
        if(StringUtils.isEmpty(userInfo)){
            return JSON.toJSONString("添加群聊联系人为空.");
        }
        List<Map<String,Object>> userList =  (List<Map<String,Object>>) JSONArray.parse(JSONArray.parseArray(userInfo).toJSONString());
        chatGroupService.save(userList);
        return JSON.toJSONString(null);
    }

    @ResponseBody
    @RequestMapping(value = "/selectUserList", method = RequestMethod.POST)
    public String selectUserList() {
        SysUserEntity sysUserEntity = new SysUserEntity();
        List<SysUserEntity> list = sysUserService.selectList(sysUserEntity);
        return JSON.toJSONString(list);
    }

    @ResponseBody
    @RequestMapping(value = "/selectGroupByUserId", method = RequestMethod.POST)
    public String selectGroupByUserId(@RequestParam("userId") String userId) {
        List<ChatGroupEntity> list = chatGroupService.selectGroupByUserId(userId);
        return JSON.toJSONString(list);
    }

}
