package com.example.demo.repository;

import com.example.demo.entity.ServiceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceLogRepository extends JpaRepository<ServiceLog, Long> {
    List<ServiceLog> findByServiceId(Long serviceId);
    List<ServiceLog> findByServiceIdAndLastCheckAfter(Long serviceId, LocalDateTime time);

    // 🌟 FIXED: Use an explicit JOIN query instead of an automated method name.
    // This maps the log's flat serviceId to the MonitoredService table ID securely.
    @Query("SELECT l FROM ServiceLog l JOIN MonitoredService s ON l.serviceId = s.id WHERE s.userEmail = :email")
    List<ServiceLog> findLogsByServiceOwnerEmail(@Param("email") String email);
}