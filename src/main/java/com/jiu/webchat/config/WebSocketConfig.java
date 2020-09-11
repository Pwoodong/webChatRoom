package com.jiu.webchat.config;

import com.jiu.webchat.webSocket.EchoHandler;
import com.jiu.webchat.webSocket.WebSocketHandshakeInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        /** 提供符合W3C标准的Websocket数据 */
        registry.addHandler(echoWebSocketHandler(), "/echo");
        //registry.addHandler(snakeWebSocketHandler(), "/snake");
        //registry.addHandler(snakeWebSocketHandler(), "/websocket");
        /** 提供符合SockJS的数据 */
        registry.addHandler(echoWebSocketHandler(), "/sockjs/echo").addInterceptors(handshakeInterceptor()).withSockJS();
        //registry.addHandler(snakeWebSocketHandler(), "/sockjs/snake").addInterceptors(handshakeInterceptor()).withSockJS();
        //registry.addHandler(snakeWebSocketHandler(), "/sockjs/websocket").addInterceptors(handshakeInterceptor()).withSockJS();
    }

    @Bean
    public WebSocketHandler echoWebSocketHandler() {
        return new EchoHandler();
    }

    @Bean
    public WebSocketHandler snakeWebSocketHandler() {
        return new PerConnectionWebSocketHandler(EchoHandler.class);
    }

    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new WebSocketHandshakeInterceptor();
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    }
}
