package com.jiu.webchat.controller;

import com.alibaba.fastjson.JSON;
import com.jiu.webchat.config.PropertiesConfig;
import com.jiu.webchat.dto.SessionUserDto;
import com.jiu.webchat.entity.ChatRecordLogEntity;
import com.jiu.webchat.service.ChatRecordLogService;
import com.jiu.webchat.webSocket.WebSocketCache;
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.jiu.webchat.config.CacheConfig.userCache;

@Controller
@RequestMapping(value = "chat")
public class ChatController {

	private static final Logger log = LoggerFactory.getLogger(ChatController.class);

	private WebSocketCache cache = WebSocketCache.me();

	@Autowired
	private ChatRecordLogService chatRecordLogService;

	@Autowired
	private PropertiesConfig propertiesConfig;

	@RequestMapping(method = RequestMethod.GET)
	public String chat(ModelMap map,HttpServletRequest request) {
		try{
			String name = request.getParameter("name");
			String groupId = request.getParameter("groupId");
			String userId = request.getParameter("userId");
			log.debug(name+"进入聊天室页面......");
			List<WebSocketSession> allList = this.cache.getGroupAll(groupId);
			map.put("msg", allList.size());
			map.put("name", name);

			/** 判断当前聊天连接人是否已经在线 */
			Boolean b = true;
			for (WebSocketSession ws : allList) {
				if(this.cache.getUserName(ws.getId()).equals(name)) {
					b = false;
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
			return "msg";
		}catch (Exception e){
			log.error("聊天室连接出现异常."+e.getMessage());
			return "404";
		}
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
		List<SessionUserDto> useDtoList = new ArrayList<>();
		for (WebSocketSession ws : all) {
			SessionUserDto s = new SessionUserDto();
			s.setId(ws.getId());
			s.setUsername(this.cache.getUserName(ws.getId()));
			s.setStatus("在线");
			useDtoList.add(s);
		}
		/** 获取缓存的所有用户信息 */
		Collections.reverse(userCache);
		useDtoList.addAll(userCache);
		Map<String, Object> rt = new HashMap<>(2);
		rt.put("size", all.size());
		rt.put("userList", ChatController.removeDuplicate(useDtoList));
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
