package com.shooraglobal.agent_database_service.service;

import com.shooraglobal.agent_database_service.dto.DeviceResponseDto;
import com.shooraglobal.agent_database_service.dto.ScreenLogRequestDto;

import com.shooraglobal.agent_database_service.dto.ScreenLogResponseDto;
import com.shooraglobal.agent_database_service.entity.Device;
import com.shooraglobal.agent_database_service.entity.ScreenLog;
import com.shooraglobal.agent_database_service.exception.AgentDatabaseServiceException;
import com.shooraglobal.agent_database_service.repo.DeviceRepo;
import com.shooraglobal.agent_database_service.repo.ScreenLogRepo;
import com.shooraglobal.agent_database_service.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;




@Service
public class ScreenLogService {
    private  final DeviceRepo deviceRepo;
    private final ScreenLogRepo screenLogRepo;
    private final FileUtil fileUtil;


    public ScreenLogService(DeviceRepo deviceRepo, ScreenLogRepo screenLogRepo, FileUtil fileUtil) {
        this.deviceRepo = deviceRepo;
        this.screenLogRepo = screenLogRepo;

        this.fileUtil = fileUtil;
    }
    @Transactional
    public String saveScreenLog(ScreenLogRequestDto dto, MultipartFile file) {

        if (file.isEmpty()) {
            throw new AgentDatabaseServiceException("Screenshot file is required");
        }

        String type = file.getContentType();

        if (type == null || !type.startsWith("image/")) {
            throw new AgentDatabaseServiceException("Wrong File Type!");
        }

        String companyName = normalizeRequired(dto.getCompanyName(), "company_name");
        String workspaceCode = normalizeRequired(dto.getWorkspaceCode(), "workspace_code");
        String userName = normalizeRequired(dto.getUserName(), "user_name");
        String email = normalizeRequired(dto.getEmail(), "email");
        String hostname = normalizeRequired(dto.getHostname(), "hostname");
        String macAddress = normalizeRequired(dto.getMacAddress(), "mac_address");
        String deviceToken = normalizeRequired(dto.getDeviceToken(), "device_token");

        dto.setCompanyName(companyName);
        dto.setWorkspaceCode(workspaceCode);
        dto.setUserName(userName);
        dto.setEmail(email);
        dto.setHostname(hostname);
        dto.setMacAddress(macAddress);
        dto.setDeviceToken(deviceToken);
        dto.setProductKey(trimOptional(dto.getProductKey()));
        dto.setRegisteredUrl(trimOptional(dto.getRegisteredUrl()));

        Device device = deviceRepo
                .findByCompanyNameIgnoreCaseAndDeviceTokenIgnoreCase(companyName, deviceToken)
                .orElseGet(Device::new);

        device.setCompanyName(companyName);

        device.setWorkspaceCode(workspaceCode);

        device.setUserName(userName);

        device.setEmail(email);

        device.setHostname(hostname);

        device.setMacAddress(macAddress);

        device.setProductKey(dto.getProductKey());

        device.setRegisteredUrl(dto.getRegisteredUrl());

        device.setDeviceToken(deviceToken);

        device.setLastScreenShotCaptureAt(dto.getCaptureTime());

        device = deviceRepo.save(device);



//      creating folder to store screenshot

        Path imagePath= null;
        try {
            imagePath = fileUtil.createFile(dto,file);
        } catch (IOException e) {
            throw new AgentDatabaseServiceException("Error in Creating File");
        }

        // SAVE DB RECORD

        ScreenLog screenLog = new ScreenLog();

        screenLog.setDevice(device);
        screenLog.setCaptureTime(dto.getCaptureTime());
        screenLog.setImagePath(imagePath.toString());

        screenLogRepo.save(screenLog);


        return "Screen Log Uploaded Successfully.";





    }


    public List<DeviceResponseDto> getAllDevices(String companyName) {

        List<Device> devices = deviceRepo.findByCompanyNameIgnoreCaseOrderByUserNameAscHostnameAsc(
                normalizeRequired(companyName, "company_name")
        );

        return devices.stream()
                .map(device -> new DeviceResponseDto(


                        device.getId(),
                        device.getCompanyName(),
                        device.getWorkspaceCode(),
                        device.getUserName(),
                        device.getEmail(),
                        device.getHostname(),
                        device.getMacAddress(),
                        device.getProductKey(),
                        device.getRegisteredUrl(),
                        device.getDeviceToken(),
                        device.getLastScreenShotCaptureAt()

                ))
                .toList();

    }

    public List<ScreenLogResponseDto> getScreenLogs(String companyName, Long deviceId,String date) {

        String normalizedCompanyName = normalizeRequired(companyName, "company_name");

        LocalDate localDate = LocalDate.parse(date);

        LocalDateTime start = localDate.atStartOfDay();

        LocalDateTime end = localDate.plusDays(1).atStartOfDay();

        List<ScreenLog> logs =
                screenLogRepo.findByDevice_IdAndDevice_CompanyNameIgnoreCaseAndCaptureTimeBetween(
                        deviceId,
                        normalizedCompanyName,
                        start,
                        end
                );

        return logs.stream()
                .map(this::toScreenLogResponseDto)
                .toList();
    }

    private ScreenLogResponseDto toScreenLogResponseDto(ScreenLog log) {

        Path path = Paths.get(log.getImagePath());

        if (!Files.exists(path)) {
            throw new AgentDatabaseServiceException("Image file not found");
        }

        try {
            String contentType = Files.probeContentType(path);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            String imageBase64 = Base64.getEncoder().encodeToString(Files.readAllBytes(path));

            return new ScreenLogResponseDto(
                    log.getId(),
                    path.getFileName().toString(),
                    contentType,
                    imageBase64,
                    log.getCaptureTime()
            );
        } catch (IOException e) {
            throw new AgentDatabaseServiceException("Error reading image file");
        }
    }

    public Resource getImage(
            String companyName,
            Long imageId,
            Long deviceId
    ) throws IOException {

        String normalizedCompanyName = normalizeRequired(companyName, "company_name");

        ScreenLog screenLog =
                screenLogRepo
                        .findByIdAndDevice_IdAndDevice_CompanyNameIgnoreCase(
                                imageId,
                                deviceId,
                                normalizedCompanyName
                        )
                        .orElseThrow(() ->
                                new AgentDatabaseServiceException(
                                        "Image not found"
                                )
                        );

        Path path = Paths.get(
                screenLog.getImagePath()
        );

        if (!Files.exists(path)) {

            throw new AgentDatabaseServiceException(
                    "Image file not found"
            );
        }

        return new UrlResource(path.toUri());
    }

    private String normalizeRequired(String value, String fieldName) {

        if (value == null || value.isBlank()) {
            throw new AgentDatabaseServiceException(fieldName + " is required");
        }

        return value.trim();
    }

    private String trimOptional(String value) {

        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        return trimmedValue.isEmpty() ? null : trimmedValue;
    }
}
