package com.shooraglobal.agent_database_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "devices",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"company_name","email", "device_token"})
        }
)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", length = 150, nullable = false)
    private String companyName;

    @Column(name = "workspace_code", length = 100, nullable = false)
    private String workspaceCode;

    @Column(name = "user_name", length = 150, nullable = false)
    private String userName;

    @Column(length = 254, nullable = false)
    private String email;

    @Column(length = 150, nullable = false)
    private String hostname;

    @Column(name = "mac_address", length = 50, nullable = false)
    private String macAddress;

    @Column(name = "product_key", length = 100)
    private String productKey;

    @Column(name = "registered_url", length = 500)
    private String registeredUrl;

    @Column(name = "device_token", length = 255, nullable = false)
    private String deviceToken;

    @Column(name = "last_screenshot_capture_at")
    private LocalDateTime lastScreenShotCaptureAt;

    @OneToMany(
            mappedBy = "device",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ScreenLog> screenLogs = new ArrayList<>();

    public Device() {
    }

    public Long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWorkspaceCode() {
        return workspaceCode;
    }

    public void setWorkspaceCode(String workspaceCode) {
        this.workspaceCode = workspaceCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getRegisteredUrl() {
        return registeredUrl;
    }

    public void setRegisteredUrl(String registeredUrl) {
        this.registeredUrl = registeredUrl;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public List<ScreenLog> getScreenLogs() {
        return screenLogs;
    }

    public void setScreenLogs(List<ScreenLog> screenLogs) {
        this.screenLogs = screenLogs;
    }

    public LocalDateTime getLastScreenShotCaptureAt() {
        return lastScreenShotCaptureAt;
    }

    public void setLastScreenShotCaptureAt(LocalDateTime lastScreenShotCaptureAt) {
        this.lastScreenShotCaptureAt = lastScreenShotCaptureAt;
    }
}
