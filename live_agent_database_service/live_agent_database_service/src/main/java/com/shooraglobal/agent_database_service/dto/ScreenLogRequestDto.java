package com.shooraglobal.agent_database_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

import java.time.LocalDateTime;

public class ScreenLogRequestDto {

    @JsonProperty("company_name")
    @NotBlank(message = "company_name is required")
    private String companyName;

    @JsonProperty("workspace_code")
    @NotBlank(message = "workspace_code is required")
    private String workspaceCode;

    @JsonProperty("user_name")
    @NotBlank(message = "user_name is required")
    private String userName;

    @JsonProperty("email")
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @JsonProperty("hostname")
    @NotBlank(message = "hostname is required")
    private String hostname;

    @JsonProperty("mac_address")
    @NotBlank(message = "mac_address is required")
    private String macAddress;

    @JsonProperty("capture_time")
    @NotNull(message = "capture_time is required")
    private LocalDateTime captureTime;

    @JsonProperty("product_key")
    private String productKey;

    @JsonProperty("registered_url")
    private String registeredUrl;

    @JsonProperty("device_token")
    @NotBlank(message = "device_token is required")
    private String deviceToken;

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

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(LocalDateTime captureTime) {
        this.captureTime = captureTime;
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
}
