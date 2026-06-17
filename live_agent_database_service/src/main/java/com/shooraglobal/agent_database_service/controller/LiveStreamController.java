package com.shooraglobal.agent_database_service.controller;

import com.shooraglobal.agent_database_service.service.RtspRelayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/live-streams")
public class LiveStreamController {

    private final RtspRelayService rtspRelayService;

    public LiveStreamController(RtspRelayService rtspRelayService) {
        this.rtspRelayService = rtspRelayService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllActiveStreams() {
        List<Map<String, Object>> streams = rtspRelayService.getActiveDeviceTokens()
                .stream()
                .map(this::buildStreamPayload)
                .collect(Collectors.toList());
        return ResponseEntity.ok(streams);
    }

    @GetMapping("/{deviceToken}")
    public ResponseEntity<Map<String, Object>> getLiveStream(@PathVariable String deviceToken) {
        return ResponseEntity.ok(buildStreamPayload(deviceToken));
    }

    private Map<String, Object> buildStreamPayload(String deviceToken) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("device_token", deviceToken);
        payload.put("active", rtspRelayService.isRelayActive(deviceToken));
        payload.put("rtsp_url", rtspRelayService.getRtspUrl(deviceToken));
        payload.put("webrtc_path", rtspRelayService.getWebRtcPath(deviceToken));
        payload.put("webrtc_url", rtspRelayService.getWebRtcUrl(deviceToken));
        return payload;
    }
}
