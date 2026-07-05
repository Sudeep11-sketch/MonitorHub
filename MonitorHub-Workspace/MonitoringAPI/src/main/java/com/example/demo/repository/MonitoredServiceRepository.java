package com.example.demo.repository;

import com.example.demo.entity.MonitoredService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MonitoredServiceRepository extends JpaRepository<MonitoredService, Long> {
    
    // 🌟 NEW: Query database records filtered specifically by owner email
    List<MonitoredService> findByUserEmail(String userEmail);
}