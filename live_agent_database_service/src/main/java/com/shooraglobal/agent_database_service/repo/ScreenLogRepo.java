package com.shooraglobal.agent_database_service.repo;

import com.shooraglobal.agent_database_service.entity.ScreenLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ScreenLogRepo
        extends JpaRepository<ScreenLog, Long> {

    List<ScreenLog> findByDevice_IdAndDevice_CompanyNameIgnoreCaseAndCaptureTimeBetween(
            Long deviceId,
            String companyName,
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<ScreenLog> findByIdAndDevice_IdAndDevice_CompanyNameIgnoreCase(
            Long id,
            Long deviceId,
            String companyName
    );
}
