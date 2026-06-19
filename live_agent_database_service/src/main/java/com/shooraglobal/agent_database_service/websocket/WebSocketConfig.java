package com.shooraglobal.agent_database_service.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final StreamWebSocketHandler streamWebSocketHandler;

    public WebSocketConfig(StreamWebSocketHandler streamWebSocketHandler) {
        this.streamWebSocketHandler = streamWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(streamWebSocketHandler, "/ws/live-ingest", "/ws/stream")
                .setAllowedOrigins("*");
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(2 * 1024 * 1024); // 2 MB
        container.setMaxTextMessageBufferSize(64 * 1024);          // 64 KB
        container.setMaxSessionIdleTimeout(300_000L);              // 5 minutes
        return container;
    }
}
