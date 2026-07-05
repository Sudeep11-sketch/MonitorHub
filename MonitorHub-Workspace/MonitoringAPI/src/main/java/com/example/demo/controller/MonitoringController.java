package com.example.demo.controller;

import com.example.demo.entity.MonitoredService;
import com.example.demo.entity.ServiceLog;
import com.example.demo.repository.ServiceLogRepository;
import com.example.demo.service.MonitoredServiceService;
import com.example.demo.scheduler.DynamicSchedulerService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
public class MonitoringController {

    private final MonitoredServiceService serviceLayer;
    private final ServiceLogRepository logRepo;
    private final DynamicSchedulerService dynamicSchedulerService;

    public MonitoringController(MonitoredServiceService serviceLayer, ServiceLogRepository logRepo, DynamicSchedulerService dynamicSchedulerService) {
        this.serviceLayer = serviceLayer;
        this.logRepo = logRepo;
        this.dynamicSchedulerService = dynamicSchedulerService;
    }

    @PostMapping
    public MonitoredService add(@RequestBody MonitoredService service) {
        MonitoredService savedService = serviceLayer.addService(service);
        dynamicSchedulerService.scheduleServiceCheck(savedService);
        return savedService;
    }

    // Filter incoming results so users only see their own metrics
    @GetMapping
    public List<MonitoredService> getAll(@RequestParam(required = false) String email) {
        if (email != null && !email.trim().isEmpty()) {
            return serviceLayer.getByUserEmail(email);
        }
        return serviceLayer.getAll();
    }
    
    // 🌟 FIXED: Pull historic table check logs specifically isolated to the active profile account owner
    @GetMapping("/logs")
    public List<ServiceLog> getAllLogs(@RequestParam(required = false) String email) {
        if (email != null && !email.trim().isEmpty()) {
            return logRepo.findLogsByServiceOwnerEmail(email);
        }
        return logRepo.findAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dynamicSchedulerService.cancelServiceCheck(id);
        serviceLayer.delete(id);
    }

    @GetMapping("/{id}/logs")
    public List<ServiceLog> getLogs(@PathVariable Long id) {
        return logRepo.findByServiceId(id);
    }
    
    @GetMapping("/{id}")
    public MonitoredService getOne(@PathVariable Long id) {
        return serviceLayer.getById(id);
    }

    // Changes a card's interval and updates its running thread on the fly!
    @PutMapping("/{id}/interval")
    public ResponseEntity<?> updateInterval(@PathVariable Long id, @RequestBody Map<String, Integer> payload) {
        if (!payload.containsKey("checkInterval")) {
            return ResponseEntity.badRequest().body("Missing checkInterval payload field key reference.");
        }
        
        int newInterval = payload.get("checkInterval");
        
        MonitoredService service = serviceLayer.getById(id);
        if (service == null) {
            return ResponseEntity.notFound().build();
        }
        
        service.setCheckInterval(newInterval);
        serviceLayer.addService(service); 
        
        dynamicSchedulerService.cancelServiceCheck(id);
        dynamicSchedulerService.scheduleServiceCheck(service);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/stats")
    public Map<String, Object> getStats(@PathVariable Long id) {
        List<ServiceLog> logs = logRepo.findByServiceId(id);

        long total = logs.size();
        long upCount = logs.stream().filter(l -> "UP".equals(l.getStatus())).count();

        double uptime = total == 0 ? 0 : (upCount * 100.0 / total);

        double avgResponse = logs.stream()
                .filter(l -> l.getResponseTime() != null)
                .mapToLong(ServiceLog::getResponseTime)
                .average()
                .orElse(0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("uptime", uptime);
        stats.put("avgResponse", avgResponse);
        stats.put("totalChecks", total);

        return stats;
    }
    
    @GetMapping("/{id}/history")
    public List<ServiceLog> getHistory(@PathVariable Long id) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return logRepo.findByServiceIdAndLastCheckAfter(id, since);
    }
}