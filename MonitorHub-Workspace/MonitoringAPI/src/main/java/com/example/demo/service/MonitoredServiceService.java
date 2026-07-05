package com.example.demo.service;

import com.example.demo.entity.MonitoredService;
import com.example.demo.repository.MonitoredServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MonitoredServiceService {

    private final MonitoredServiceRepository repo;
    
    public MonitoredServiceService(MonitoredServiceRepository repo) {
        this.repo = repo;
    }

    public MonitoredService addService(MonitoredService service) {
        // If it's a completely new service with no status yet, default it to UNKNOWN
        if (service.getStatus() == null) {
            service.setStatus("UNKNOWN");
        }
        return repo.save(service);
    }

    public List<MonitoredService> getAll() {
        return repo.findAll();
    }
    
    // 🌟 NEW: Pass the email parameter straight down into your custom repository query method
    public List<MonitoredService> getByUserEmail(String email) {
        return repo.findByUserEmail(email);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public MonitoredService getById(Long id) {
        return repo.findById(id).orElse(null);
    }
}