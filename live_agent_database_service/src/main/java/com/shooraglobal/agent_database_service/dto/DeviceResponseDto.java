package com.shooraglobal.agent_database_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class DeviceResponseDto {
    private long id;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("workspace_code")
    private String workspaceCode;

    @JsonProperty("user_name")
    private String userName;

    private String email;

    private String hostname;

    @JsonProperty("mac_address")
    private String macAddress;

    @JsonProperty("product_key")
    private String productKey;

    @JsonProperty("registered_url")
    private String registeredUrl;

    @JsonProperty("device_token")
    private String deviceToken;

    @JsonProperty("last_screenshot_capture_at")
    private LocalDateTime lastScreenShotCaptureAt;

    public DeviceResponseDto() {
    }


    public DeviceResponseDto(
            long id,
            String companyName,
            String workspaceCode,
            String userName,
            String email,
            String hostname,
            String macAddress,
            String productKey,
            String registeredUrl,
            String deviceToken,
            LocalDateTime lastScreenShotCaptureAt
    ) {
        this.id = id;
        this.companyName = companyName;
        this.workspaceCode = workspaceCode;
        this.userName = userName;
        this.email = email;
        this.hostname = hostname;
        this.macAddress = macAddress;
        this.productKey = productKey;
        this.registeredUrl = registeredUrl;
        this.deviceToken = deviceToken;
        this.lastScreenShotCaptureAt = lastScreenShotCaptureAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public LocalDateTime getLastScreenShotCaptureAt() {
        return lastScreenShotCaptureAt;
    }

    public void setLastScreenShotCaptureAt(LocalDateTime lastScreenShotCaptureAt) {
        this.lastScreenShotCaptureAt = lastScreenShotCaptureAt;
    }
}
