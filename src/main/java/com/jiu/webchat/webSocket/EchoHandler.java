package com.jiu.webchat.webSocket;

import com.jiu.webchat.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * websocket消息处理器类
 */
public class EchoHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(EchoHandler.class);

    /**
     * 登录者信息缓存，主要用于把session id和昵称关联起来，用于发送消息
     */
    private final WebSocketCache cache = WebSocketCache.me();

    /**
     * 记录每个session id发送过快的时间，防止用户恶意刷屏
     */
    private final Map<String, Date> timeLimitMap = new Hashtable<>();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String msg = message.getPayload().trim();
            log.debug("你说的内容:{}", msg);
            String responseMessage = null;
            /** 用于限制用户发送信息的频率，防止恶意刷屏 */
            Date timeLimit = DateUtils.addSeconds(new Date(), -2);
            if (null != msg && !"".equals(msg.trim())) {
                if (msg.length() > 200) {
                    session.sendMessage(new TextMessage("太长，刷屏是不对的（这条只有你能看到，嘿嘿）"));
                    return;
                }
                boolean before = timeLimit.before(this.timeLimitMap.get(session.getId()));
                log.debug("是否过快{}", before);
                if (before) {
                    session.sendMessage(new TextMessage("说的太频繁了！刷屏是不对的！"));
                    return;
                }
                String name = this.cache.getUserName(session.getId());
                responseMessage = name + "说：" + msg;
                this.timeLimitMap.put(session.getId(), new Date());
            }
            if (null != responseMessage && !"".equals(responseMessage.trim())) {
                log.debug("回复内容:{}", responseMessage);
                String now = DateUtils.date2String(new Date());
                responseMessage = now + " " + responseMessage;
                TextMessage textMessage = new TextMessage(responseMessage);
                String groupId = (String) session.getAttributes().get("USER_GROUP");
                this.noticeGroupMsg(groupId,textMessage);
            }
        } catch (Exception e) {
            log.error("发送异常:", e);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("建立链接");
        Map<String, Object> attributes = session.getAttributes();
        if (null == attributes || attributes.isEmpty()) {
            log.debug("没有参数....");
        } else {
            String groupId = (String) session.getAttributes().get("USER_GROUP");
            String userId = (String) session.getAttributes().get("USER_ID");
            String userName = (String) session.getAttributes().get("USER_NAME");
            if(StringUtils.isNotEmpty(groupId) && StringUtils.isNotEmpty(userName)){
                /** 判断当前连接是否已经在线 */
                List<WebSocketSession> allList = this.cache.getGroupAll(groupId);
                Boolean b = true;
                for (WebSocketSession ws : allList) {
                    if(this.cache.getUserName(ws.getId()).equals(userName)) {
                        b = false;
                    }
                }
                if(b){
                    this.cache.addCache(groupId,userName, session);
                    this.timeLimitMap.put(session.getId(), new Date(0));
                    TextMessage textMessage = new TextMessage(userName+ "上线啦!");
                    this.noticeGroupMsg(groupId,textMessage);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.debug("关闭链接");
        try{
            this.closeAndNotice(session);
        }catch (Exception e){
            log.error("关闭连接出现异常."+e.getMessage());
        }
    }


    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        log.debug("handleBinaryMessage:{}", message.toString());
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.debug("handlePongMessage:{}", message.toString());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("handleTransportError:", exception);
        try{
            this.closeAndNotice(session);
        }catch (Exception e){
            log.error("handleTransportError 出现异常."+e.getMessage());
        }
    }

    /**
     * 通知消息给群组内人员
     * @param    groupId        群组ID
     * @param    textMessage    通知消息
     * @return   void
     **/
    private void noticeGroupMsg(String groupId,TextMessage textMessage) {
        this.cache.getGroupAll(groupId).forEach(s -> {
            try {
                log.debug("发送消息，sessionid:{}", s.getId());
                if (s.isOpen()) {
                    s.sendMessage(textMessage);
                }
            } catch (Exception e) {
                log.error("发送异常", e);
            }
        });
    }

    /**
     * 关闭连接并且通知群组内其它在线人员
     * @param    session
     * @return   void
     * @throws   Exception
     **/
    private void closeAndNotice(WebSocketSession session) throws Exception {
        String name = this.cache.getUserName(session.getId());
        this.cache.deleteCache(session.getId());
        this.timeLimitMap.remove(session.getId());
        if (StringUtils.isNotEmpty(name)) {
            String groupId = (String) session.getAttributes().get("USER_GROUP");
            if(StringUtils.isNotEmpty(groupId)){
                this.cache.getGroupAll(groupId).forEach(s -> {
                    try {
                        String now = DateUtils.date2String(new Date());
                        s.sendMessage(new TextMessage(now + " " + name + "下线啦!"));
                    } catch (IOException e) {
                        log.error("异常", e);
                    }
                });
            }
            if (session.isOpen()) {
                session.close();
            }
        }
    }

}
