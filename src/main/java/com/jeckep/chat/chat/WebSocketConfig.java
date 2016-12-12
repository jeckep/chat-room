package com.jeckep.chat.chat;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ChatWebSocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //TODO investigate HttpSessionHandshakeInterceptor(list of attributes)
        registry.addHandler(webSocketHandler, "/chat/",
                "https:/jeckep.online/chat/", "ws://jeckep.online.chat",
                "jeckep.online/chat/", "http://mobydock.chat/")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }
}