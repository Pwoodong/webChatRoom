package com.jiu.webchat.webSocket;

import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户信息缓存
 */
public class WebSocketCache {

    private static WebSocketCache me;
    private final Map<String, Object[]> cacheMap;

    private WebSocketCache() {
        this.cacheMap = new HashMap<>();
    }

    public static WebSocketCache me() {
        if (null == me) {
            me = new WebSocketCache();
        }
        return me;
    }

    public void addCache(String userName, WebSocketSession session) {
        if (!this.cacheMap.containsKey(session.getId())) {
            Object[] objects = new Object[]{userName, session};
            this.cacheMap.put(session.getId(), objects);
        }
    }

    public void addCache(String groupId, String userName, WebSocketSession session) {
        if (!this.cacheMap.containsKey(session.getId())) {
            Object[] objects = new Object[]{userName, session, groupId};
            this.cacheMap.put(session.getId(), objects);
        }
    }

    public String getUserName(String id) {
        final String[] name = {null};
        this.cacheMap.forEach((key, value) -> {
            if (key.equals(id)) {
                name[0] = value[0].toString();
            }
        });
        return name[0];
    }

    public List<WebSocketSession> getAll() {
        return this.cacheMap.values().stream().map(obj -> (WebSocketSession) obj[1]).collect(Collectors.toList());
    }

    public List<WebSocketSession> getGroupAll(String groupId) {
        List<WebSocketSession> allList = getAll();
        List<WebSocketSession> resultList = new ArrayList<>();
        for (WebSocketSession webSocketSession : allList) {
            if (webSocketSession.isOpen()) {
                Map<String, Object> map = webSocketSession.getAttributes();
                if (map.get("USER_GROUP").equals(groupId)) {
                    resultList.add(webSocketSession);
                }
            }
        }
        return resultList;
    }

    public void deleteCache(String id) {
        this.cacheMap.remove(id);
    }
}
