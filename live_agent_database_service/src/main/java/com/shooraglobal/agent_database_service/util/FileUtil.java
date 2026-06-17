package com.shooraglobal.agent_database_service.util;

import com.shooraglobal.agent_database_service.dto.ScreenLogRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
@Component
public class FileUtil {


    @Value("${storage.path}")
    private  String BASE_DIR;


    public Path createFile(ScreenLogRequestDto dto, MultipartFile file) throws IOException {
        System.out.println("File is creating ...");
        LocalDate captureDate = dto.getCaptureTime().toLocalDate();
        Path folderPath = Paths.get(BASE_DIR)
                .resolve(sanitize(dto.getCompanyName()))
                .resolve(sanitize(dto.getUserName()))
                .resolve(captureDate.toString());

        Files.createDirectories(folderPath);

        // FILE NAME

        String fileName =
                dto.getCaptureTime()
                        .format(DateTimeFormatter.ofPattern("HH-mm-ss"))
                        + getFileExtension(file);

        Path imagePath = folderPath.resolve(fileName);



        // SAVE FILE

        Files.copy(
                file.getInputStream(),
                imagePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        System.out.println("File is created...");

        return imagePath;
    }

    public  String sanitize(String value) {

        if (value == null || value.isBlank()) {
            return "unknown";
        }

        return value.replaceAll("[^a-zA-Z0-9-_]", "_");
    }

    private String getFileExtension(MultipartFile file) {

        String contentType = file.getContentType();

        if ("image/jpeg".equalsIgnoreCase(contentType)) {
            return ".jpeg";
        }

        if ("image/png".equalsIgnoreCase(contentType)) {
            return ".png";
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');

            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                String extension = originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);

                if (extension.matches("\\.[a-z0-9]{1,10}")) {
                    return extension;
                }
            }
        }

        return ".img";
    }
}
