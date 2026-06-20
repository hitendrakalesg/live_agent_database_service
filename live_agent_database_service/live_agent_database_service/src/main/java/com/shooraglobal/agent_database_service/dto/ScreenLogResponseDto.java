package com.shooraglobal.agent_database_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ScreenLogResponseDto {

    private Long id;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("image_base64")
    private String imageBase64;

    @JsonProperty("capture_time")
    private LocalDateTime captureTime;

    public ScreenLogResponseDto() {
    }

    public ScreenLogResponseDto(
            Long id,
            String fileName,
            String contentType,
            String imageBase64,
            LocalDateTime captureTime
    ) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.imageBase64 = imageBase64;
        this.captureTime = captureTime;
    }

    public Long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public void setCaptureTime(LocalDateTime captureTime) {
        this.captureTime = captureTime;
    }
}
