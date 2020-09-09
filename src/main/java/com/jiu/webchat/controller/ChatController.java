package com.jiu.webchat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.jiu.webchat.config.PropertiesConfig;
import com.jiu.webchat.entity.ChatRecordLogEntity;
import com.jiu.webchat.entity.SysUserEntity;
import com.jiu.webchat.service.ChatRecordLogService;
import com.jiu.webchat.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.jiu.webchat.dto.SessionUserDto;

import com.jiu.webchat.webSocket.WebSocketCache;

@Controller
@RequestMapping(value = "chat")
public class ChatController {

	private static final Logger log = LoggerFactory.getLogger(ChatController.class);

	private WebSocketCache cache = WebSocketCache.me();

	@Autowired
	private ChatRecordLogService chatRecordLogService;

	@Autowired
	private PropertiesConfig propertiesConfig;

	@Autowired
	private SysUserService sysUserService;

	@RequestMapping(method = RequestMethod.GET)
	public String chat(ModelMap map,HttpServletRequest request) {
		String name = request.getParameter("name");
		String groupId = request.getParameter("groupId");
		String userId = request.getParameter("userId");
		log.debug(name+"进入聊天室页面......");
		map.put("msg", this.cache.getAll().size());
		map.put("name", name);
		List<WebSocketSession> all = this.cache.getAll();
		List<SessionUserDto> useDtos = new ArrayList<>();
		Boolean b = true;
		if (all != null && all.size() > 0) {
			for (WebSocketSession ws : all) {
				SessionUserDto s = new SessionUserDto();
				s.setId(ws.getId());
				s.setUsername(this.cache.getUserName(ws.getId()));
				if(this.cache.getUserName(ws.getId()).equals(name)) {
					b=false;
				}
				s.setStatus("在线");
				useDtos.add(s);
			}
		}
		
		if(b) {
			ChatRecordLogEntity chatRecordLogEntity = new ChatRecordLogEntity();
			chatRecordLogEntity.setGroupId(groupId);
			map.put("list", chatRecordLogService.selectByList(chatRecordLogEntity));
			map.put("groupId", groupId);
			map.put("userId", userId);
			return "chat";
		}
		return "404";
	}

	/**
	 * 轮询获取当前在线用户数
	 * 
	 * @return 在线用户数
	 */
	@ResponseBody
	@RequestMapping(value = "/getSum", method = RequestMethod.POST)
	public String getSum(@RequestParam("groupId") String groupId) {
		List<WebSocketSession> all = this.cache.getGroupAll(groupId);
		int size = all.size();
		List<SessionUserDto> useDtos = new ArrayList<>();
		if (all != null && all.size() > 0) {
			for (WebSocketSession ws : all) {
				SessionUserDto s = new SessionUserDto();
				s.setId(ws.getId());
				s.setUsername(this.cache.getUserName(ws.getId()));
				s.setStatus("在线");
				useDtos.add(s);
			}
		}
		/** 查询所有的账号存入列表 事实查询对数据库压力太大 要做初始化 */
		SysUserEntity sysUserEntity = new SysUserEntity();
		List<SessionUserDto> haveCount = sysUserService.selectSessionUserList(sysUserEntity);
		useDtos.addAll(haveCount);
		Map<String, Object> rt = new HashMap<>(2);
		rt.put("size", size);
		rt.put("userList", ChatController.removeDuplicate(useDtos));
		return JSON.toJSONString(rt);
	}

	public static List<SessionUserDto> removeDuplicate(List<SessionUserDto> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).getUsername().equals(list.get(i).getUsername())&&list.get(j).getStatus().equals("离线")) {
					list.remove(j);
				}
			}
		}
		return list;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(ModelMap map) {
		log.debug("进入登录页面......");
		map.put("msg", this.cache.getAll().size());
		return "login";
	}

	@ResponseBody
	@RequestMapping(value = "/saveLog", method = RequestMethod.POST)
	public String saveLog(@RequestParam("name") String name, @RequestParam("text") String text,
						  @RequestParam("groupId") String groupId,@RequestParam("userId") String userId) {
		List<WebSocketSession> all = this.cache.getAll();
		List<SessionUserDto> useDtos = new ArrayList<>();
		Boolean b = false;
		if (all != null && all.size() > 0) {
			for (WebSocketSession ws : all) {
				SessionUserDto s = new SessionUserDto();
				s.setId(ws.getId());
				s.setUsername(this.cache.getUserName(ws.getId()));
				if(this.cache.getUserName(ws.getId()).equals(name)) {
					b=true;
				}
				s.setStatus("在线");
				useDtos.add(s);
			}
		}
		if(b) {
			ChatRecordLogEntity chatRecordLogEntity = new ChatRecordLogEntity();
			chatRecordLogEntity.setGroupId(groupId);
			chatRecordLogEntity.setUserId(userId);
			chatRecordLogEntity.setName(name);
			chatRecordLogEntity.setText(text);
			chatRecordLogEntity.setType("1");
			chatRecordLogEntity.setDistrict(propertiesConfig.getDistrict());
			chatRecordLogService.insert(chatRecordLogEntity);
		}
		return JSON.toJSONString(null);
	}

	@ResponseBody
	@RequestMapping(value = "/getFile", method = RequestMethod.GET)
	public String getFile() {
		ChatRecordLogEntity chatRecordLogEntity = new ChatRecordLogEntity();
		chatRecordLogEntity.setType("2");
		List<ChatRecordLogEntity> haveCount = chatRecordLogService.selectByList(chatRecordLogEntity);
		Map<String, Object> rt = new HashMap<>(1);
		rt.put("list", haveCount);
		return JSON.toJSONString(rt);
	}

	@ResponseBody
	@RequestMapping(value = "/getChatRecord",method = RequestMethod.POST)
	public String getChatRecord(@RequestParam("groupId") String groupId) {
		ChatRecordLogEntity chatRecordLogEntity = new ChatRecordLogEntity();
		chatRecordLogEntity.setGroupId(groupId);
		List<ChatRecordLogEntity> list = chatRecordLogService.selectByList(chatRecordLogEntity);
		return JSON.toJSONString(list);
	}

}