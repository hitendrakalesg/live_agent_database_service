package com.shooraglobal.agent_database_service.service;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RtspRelayService {

    private static final Logger log = LoggerFactory.getLogger(RtspRelayService.class);

    @Value("${mediamtx.rtsp.url:rtsp://localhost:8554}")
    private String mediamtxRtspUrl;

    @Value("${mediamtx.rtsp.path-prefix:live}")
    private String mediamtxPathPrefix;

    @Value("${mediamtx.webrtc.public.url:}")
    private String mediamtxWebRtcPublicUrl;

    @Value("${ffmpeg.path:ffmpeg}")
    private String ffmpegPath;

    private final ConcurrentHashMap<String, Process> relayProcesses = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, OutputStream> relayStdins = new ConcurrentHashMap<>();

    public boolean startRelay(String deviceToken) {
        stopRelay(deviceToken);

        String sanitizedToken = sanitizeToken(deviceToken);
        String rtspTarget = buildRtspTarget(sanitizedToken);

        try {
            List<String> command = List.of(
                    firstNonBlank(ffmpegPath, "ffmpeg"),
                    "-hide_banner",
                    "-loglevel", "warning",
                    "-fflags", "nobuffer",
                    "-flags", "low_delay",
                    "-f", "mpegts",
                    "-i", "pipe:0",
                    "-map", "0:v:0",
                    "-c:v", "copy",
                    "-an",
                    "-f", "rtsp",
                    "-rtsp_transport", "tcp",
                    rtspTarget
            );

            Process process = new ProcessBuilder(command).start();
            relayProcesses.put(deviceToken, process);
            relayStdins.put(deviceToken, process.getOutputStream());
            startErrorLogger(process, sanitizedToken);

            // Give FFmpeg 300ms to fail fast (e.g. binary not found, RTSP server unreachable)
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            if (!process.isAlive()) {
                log.error("RTSP relay process exited immediately for deviceToken={} — check ffmpeg path and MediaMTX on port 8554",
                        sanitizedToken);
                stopRelay(deviceToken);
                return false;
            }

            log.info("RTSP relay started: deviceToken={} -> {}", sanitizedToken, rtspTarget);
            return true;
        } catch (IOException e) {
            log.error("Failed to start RTSP relay for deviceToken={}", sanitizedToken, e);
            return false;
        }
    }

    public boolean writeStream(String deviceToken, byte[] data) {
        OutputStream stdin = relayStdins.get(deviceToken);
        if (stdin == null) {
            return false;
        }

        try {
            stdin.write(data);
            stdin.flush();
            return true;
        } catch (IOException e) {
            log.warn("Live stream write failed for deviceToken={}: {}; stopping relay",
                    sanitizeToken(deviceToken), e.getMessage());
            stopRelay(deviceToken);
            return false;
        }
    }

    public void stopRelay(String deviceToken) {
        OutputStream stdin = relayStdins.remove(deviceToken);
        Process process = relayProcesses.remove(deviceToken);

        try {
            if (stdin != null) {
                stdin.close();
            }
        } catch (IOException ignored) {
        }

        if (process != null) {
            process.destroyForcibly();
            log.info("RTSP relay stopped: deviceToken={}", sanitizeToken(deviceToken));
        }
    }

    public boolean isRelayActive(String deviceToken) {
        Process process = relayProcesses.get(deviceToken);
        return process != null && process.isAlive();
    }

    public List<String> getActiveDeviceTokens() {
        return relayProcesses.entrySet().stream()
                .filter(e -> e.getValue().isAlive())
                .map(java.util.Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toList());
    }

    public String getRtspUrl(String deviceToken) {
        return buildRtspTarget(sanitizeToken(deviceToken));
    }

    public String getWebRtcPath(String deviceToken) {
        String token = sanitizeToken(deviceToken);
        String prefix = sanitizePathPrefix(mediamtxPathPrefix);
        return prefix.isBlank() ? "/" + token : "/" + prefix + "/" + token;
    }

    public String getWebRtcUrl(String deviceToken) {
        if (mediamtxWebRtcPublicUrl == null || mediamtxWebRtcPublicUrl.isBlank()) {
            return "";
        }

        return removeTrailingSlash(mediamtxWebRtcPublicUrl.trim()) + getWebRtcPath(deviceToken);
    }

    @PreDestroy
    public void cleanup() {
        log.info("Stopping all RTSP relays on shutdown");
        for (String token : relayStdins.keySet().toArray(new String[0])) {
            stopRelay(token);
        }
    }

    private void startErrorLogger(Process process, String sanitizedToken) {
        Thread stderrLogger = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.warn("ffmpeg-relay [{}]: {}", sanitizedToken, line);
                }
            } catch (IOException ignored) {
            }
        }, "ffmpeg-relay-stderr-" + sanitizedToken);
        stderrLogger.setDaemon(true);
        stderrLogger.start();
    }

    private String buildRtspTarget(String sanitizedToken) {
        String baseUrl = removeTrailingSlash(firstNonBlank(mediamtxRtspUrl, "rtsp://localhost:8554"));
        String prefix = sanitizePathPrefix(mediamtxPathPrefix);
        return prefix.isBlank()
                ? baseUrl + "/" + sanitizedToken
                : baseUrl + "/" + prefix + "/" + sanitizedToken;
    }

    private String sanitizeToken(String token) {
        return token == null || token.isBlank()
                ? "unknown"
                : token.replaceAll("[^a-zA-Z0-9\\-_]", "").toLowerCase();
    }

    private String sanitizePathPrefix(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.replaceAll("[^a-zA-Z0-9\\-_/]", "")
                .replaceAll("^/+", "")
                .replaceAll("/+$", "");
    }

    private String removeTrailingSlash(String value) {
        String currentValue = value;
        while (currentValue.endsWith("/")) {
            currentValue = currentValue.substring(0, currentValue.length() - 1);
        }
        return currentValue;
    }

    private String firstNonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
