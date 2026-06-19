package com.shooraglobal.agent_database_service.websocket;

import com.shooraglobal.agent_database_service.service.RtspRelayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;

@Component
public class StreamWebSocketHandler extends BinaryWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(StreamWebSocketHandler.class);
    private static final String ATTR_DEVICE_TOKEN = "deviceToken";

    private final RtspRelayService rtspRelayService;

    @Value("${live.stream.agent-secret:}")
    private String expectedAgentSecret;

    @Value("${live.stream.screen-key:}")
    private String expectedScreenKey;

    public StreamWebSocketHandler(RtspRelayService rtspRelayService) {
        this.rtspRelayService = rtspRelayService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String deviceToken = session.getHandshakeHeaders().getFirst("X-Device-Token");
        if (deviceToken == null || deviceToken.isBlank()) {
            log.warn("Agent connected without X-Device-Token, closing session {}", session.getId());
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Missing X-Device-Token"));
            return;
        }

        if (!matchesConfiguredSecret(expectedAgentSecret, session.getHandshakeHeaders().getFirst("X-Agent-Secret"))) {
            log.warn("Agent stream rejected for deviceToken={}: invalid X-Agent-Secret", sanitizeForLog(deviceToken));
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Invalid agent secret"));
            return;
        }

        if (!matchesConfiguredSecret(expectedScreenKey, session.getHandshakeHeaders().getFirst("X-SGFortress-Screen-Key"))) {
            log.warn("Agent stream rejected for deviceToken={}: invalid screen key", sanitizeForLog(deviceToken));
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Invalid screen key"));
            return;
        }

        String container = session.getHandshakeHeaders().getFirst("X-Stream-Container");
        if (container != null && !container.isBlank() && !"mpegts".equalsIgnoreCase(container.trim())) {
            log.warn("Agent stream rejected for deviceToken={}: unsupported container={}", sanitizeForLog(deviceToken), container);
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Unsupported stream container"));
            return;
        }

        session.getAttributes().put(ATTR_DEVICE_TOKEN, deviceToken);
        if (!rtspRelayService.startRelay(deviceToken)) {
            session.close(CloseStatus.SERVER_ERROR.withReason("Unable to start RTSP relay"));
            return;
        }

        log.info("Agent live stream connected: deviceToken={}", sanitizeForLog(deviceToken));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String deviceToken = (String) session.getAttributes().get(ATTR_DEVICE_TOKEN);
        if (deviceToken == null) {
            return;
        }

        ByteBuffer payload = message.getPayload();
        byte[] streamData = new byte[payload.remaining()];
        payload.get(streamData);
        rtspRelayService.writeStream(deviceToken, streamData);

        // If the relay died during or before this write (e.g. FFmpeg crashed, MediaMTX restarted),
        // close the WebSocket so the agent's supervisor loop reconnects and a fresh relay is started.
        if (!rtspRelayService.isRelayActive(deviceToken)) {
            log.warn("RTSP relay died for deviceToken={}; closing WebSocket to trigger agent reconnect",
                    sanitizeForLog(deviceToken));
            try {
                session.close(CloseStatus.SERVER_ERROR.withReason("RTSP relay stopped"));
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.debug("Ignoring text message from live stream session {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String deviceToken = (String) session.getAttributes().get(ATTR_DEVICE_TOKEN);
        if (deviceToken != null) {
            rtspRelayService.stopRelay(deviceToken);
            log.info("Agent live stream disconnected: deviceToken={}, status={}", sanitizeForLog(deviceToken), status);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String deviceToken = (String) session.getAttributes().get(ATTR_DEVICE_TOKEN);
        log.warn("Live stream transport error: deviceToken={}, error={}", sanitizeForLog(deviceToken), exception.getMessage());
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    private boolean matchesConfiguredSecret(String expectedValue, String actualValue) {
        if (expectedValue == null || expectedValue.isBlank()) {
            return true;
        }

        return actualValue != null && expectedValue.trim().equals(actualValue.trim());
    }

    private String sanitizeForLog(String value) {
        return value == null ? "" : value.replaceAll("[^a-zA-Z0-9._-]", "");
    }
}
