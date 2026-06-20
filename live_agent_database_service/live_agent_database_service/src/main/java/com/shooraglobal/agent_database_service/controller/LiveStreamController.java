package com.shooraglobal.agent_database_service.controller;

import com.shooraglobal.agent_database_service.entity.Device;
import com.shooraglobal.agent_database_service.repo.DeviceRepo;
import com.shooraglobal.agent_database_service.service.RtspRelayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/live-streams")
public class LiveStreamController {

    private final RtspRelayService rtspRelayService;
    private final DeviceRepo deviceRepo;

    public LiveStreamController(RtspRelayService rtspRelayService, DeviceRepo deviceRepo) {
        this.rtspRelayService = rtspRelayService;
        this.deviceRepo = deviceRepo;
    }

    /**
     * GET /api/live-streams
     *   → all currently active streams (all companies), enriched with employee info
     *
     * GET /api/live-streams?companyName=Acme
     *   → active streams for that company only, sorted by employee name then hostname
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllActiveStreams(
            @RequestParam(required = false) String companyName) {

        List<Map<String, Object>> streams;

        if (companyName != null && !companyName.isBlank()) {
            // Fetch all devices for this company from DB, keep only those streaming right now
            streams = deviceRepo
                    .findByCompanyNameIgnoreCaseOrderByUserNameAscHostnameAsc(companyName)
                    .stream()
                    .filter(device -> rtspRelayService.isRelayActive(device.getDeviceToken()))
                    .map(device -> buildStreamPayload(device.getDeviceToken(), device))
                    .collect(Collectors.toList());
        } else {
            // No filter: all active relay tokens, enriched with DB info where available
            streams = rtspRelayService.getActiveDeviceTokens()
                    .stream()
                    .map(token -> {
                        Device device = deviceRepo.findByDeviceTokenIgnoreCase(token).orElse(null);
                        return buildStreamPayload(token, device);
                    })
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(streams);
    }

    /**
     * GET /api/live-streams/{deviceToken}
     *   → stream status + WebRTC URL + employee info for a single device
     */
    @GetMapping("/{deviceToken}")
    public ResponseEntity<Map<String, Object>> getLiveStream(@PathVariable String deviceToken) {
        Device device = deviceRepo.findByDeviceTokenIgnoreCase(deviceToken).orElse(null);
        return ResponseEntity.ok(buildStreamPayload(deviceToken, device));
    }

    private Map<String, Object> buildStreamPayload(String deviceToken, Device device) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("device_token", deviceToken);
        payload.put("active", rtspRelayService.isRelayActive(deviceToken));
        payload.put("webrtc_url", rtspRelayService.getWebRtcUrl(deviceToken));
        payload.put("webrtc_path", rtspRelayService.getWebRtcPath(deviceToken));
        payload.put("rtsp_url", rtspRelayService.getRtspUrl(deviceToken));

        if (device != null) {
            payload.put("user_name", device.getUserName());
            payload.put("email", device.getEmail());
            payload.put("hostname", device.getHostname());
            payload.put("company_name", device.getCompanyName());
            payload.put("workspace_code", device.getWorkspaceCode());
            payload.put("mac_address", device.getMacAddress());
        }

        return payload;
    }
}
