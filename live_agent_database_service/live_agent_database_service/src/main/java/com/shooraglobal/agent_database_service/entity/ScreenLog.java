package com.shooraglobal.agent_database_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "screen_logs")
public class ScreenLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime captureTime;

    @Column(length = 500)
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    private LocalDateTime createdAt = LocalDateTime.now();

    public ScreenLog() {
    }

    public ScreenLog(
            LocalDateTime captureTime,
            String imagePath,
            Device device
    ) {
        this.captureTime = captureTime;
        this.imagePath = imagePath;
        this.device = device;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(LocalDateTime captureTime) {
        this.captureTime = captureTime;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}