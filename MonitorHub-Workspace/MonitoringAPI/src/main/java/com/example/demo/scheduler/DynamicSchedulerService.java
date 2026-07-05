package com.example.demo.scheduler;

import com.example.demo.entity.MonitoredService;
import com.example.demo.repository.MonitoredServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class DynamicSchedulerService {

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private MonitoredServiceRepository serviceRepository;

    @Autowired
    private HealthChecker healthChecker;

    // Keeps track of active tasks to allow modifying or deleting them dynamically
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    // 1. Kick off all targets systematically when Spring Boot finishes booting up
    @EventListener(ApplicationReadyEvent.class)
    public void initExistingSchedules() {
        System.out.println("🚀 Initializing target monitor tasks pool...");
        serviceRepository.findAll().forEach(this::scheduleServiceCheck);
    }

    // 2. Schedule or update a target's interval timing loop
    public void scheduleServiceCheck(MonitoredService service) {
        cancelServiceCheck(service.getId()); // Clear existing routine if updating

        // FIXED: Extract the ID via .getId() to match the signature in HealthChecker
        Runnable task = () -> healthChecker.performSingleCheck(service.getId());

        // Map interval execution safely
        ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(
                task, 
                Duration.ofSeconds(service.getCheckInterval())
        );

        scheduledTasks.put(service.getId(), future);
        System.out.println("⏰ Registered [" + service.getName() + "] execution loop every " + service.getCheckInterval() + "s");
    }

    // 3. Halt a background checking loop if a target gets deleted
    public void cancelServiceCheck(Long serviceId) {
        if (scheduledTasks.containsKey(serviceId)) {
            ScheduledFuture<?> future = scheduledTasks.get(serviceId);
            if (future != null) future.cancel(true);
            scheduledTasks.remove(serviceId);
        }
    }
}