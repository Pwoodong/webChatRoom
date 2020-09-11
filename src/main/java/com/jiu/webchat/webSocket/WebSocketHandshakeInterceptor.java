package com.jiu.webchat.webSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.debug("beforeHandshake..............");
        if (request instanceof ServletServerHttpRequest) {
            String groupId = ((ServletServerHttpRequest) request).getServletRequest().getParameter("groupId");
            String userId = ((ServletServerHttpRequest) request).getServletRequest().getParameter("userId");
            String userName = ((ServletServerHttpRequest) request).getServletRequest().getParameter("name");
            log.error("登录用户信息【groupId:" + groupId + ",userId:" + userId + "," + userName + "】");
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();
            log.error("开始拦截处理【" + session + "】");
            if (session != null) {
                if (groupId != null) {
                    session.setAttribute("USER_GROUP", groupId);
                    attributes.put("USER_GROUP", groupId);
                }
                session.setAttribute("USER_ID", userId);
                session.setAttribute("USER_NAME", userName);
                attributes.put("USER_ID", userId);
                attributes.put("USER_NAME", userName);
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("afterHandshake: " + wsHandler + "exceptions: " + exception);
    }

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            HttpServletRequest servletRequest = serverRequest.getServletRequest();
            String requestURI = servletRequest.getRequestURI();
            log.debug("requestURL;{}", requestURI);
            Enumeration<String> parameterNames = servletRequest.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                Object value = servletRequest.getAttribute(name);
                log.debug("parameterName:{}, parameterName:{}", name, value);
            }
            return servletRequest.getSession(false);
        }
        return null;
    }
}
