package com.example.demo.scheduler;

import com.example.demo.entity.AlertContact;
import com.example.demo.entity.MonitoredService;
import com.example.demo.entity.ServiceLog;
import com.example.demo.repository.AlertContactRepository;
import com.example.demo.repository.MonitoredServiceRepository;
import com.example.demo.repository.ServiceLogRepository;
import com.example.demo.service.SmsNotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class HealthChecker {

    private static final Logger logger = LoggerFactory.getLogger(HealthChecker.class);
    
    private final MonitoredServiceRepository repo;
    private final ServiceLogRepository logRepo;
    private final AlertContactRepository contactRepo; 
    private final SmsNotificationService smsService;

    public HealthChecker(MonitoredServiceRepository repo, ServiceLogRepository logRepo, AlertContactRepository contactRepo, SmsNotificationService smsService) {
        this.repo = repo;
        this.logRepo = logRepo;
        this.contactRepo = contactRepo; 
        this.smsService = smsService;
    }

    @Transactional
    public void performSingleCheck(Long serviceId) {
        MonitoredService s = repo.findById(serviceId).orElse(null);
        if (s == null) return;

        String oldStatus = s.getStatus();
        long start = System.currentTimeMillis();
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(s.getUrl()).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int httpStatusCode = connection.getResponseCode(); 
            int responseTime = (int) (System.currentTimeMillis() - start);
            
            // Evaluate if status is healthy (2xx or 3xx)
            boolean isHealthy = (httpStatusCode >= 200 && httpStatusCode < 400);
            String status = isHealthy ? "UP" : "DOWN";

            // Determine diagnostic details
            String diagnosticMessage = isHealthy ? "OK" : "HTTP Status Code: " + httpStatusCode + " " + connection.getResponseMessage();

            // 1. Update Parent Entity
            s.setStatus(status);
            s.setResponseTime(responseTime);
            s.setLastCheck(LocalDateTime.now());
            repo.save(s);

            // 2. Append Custom Log Entry
            ServiceLog log = new ServiceLog();
            log.setServiceId(s.getId());
            log.setStatus(status);
            log.setResponseTime(responseTime);
            log.setLastCheck(LocalDateTime.now());
            log.setErrorMessage(diagnosticMessage);
            logRepo.save(log);

            logger.info("Service {} is {}. Details: {}", s.getName(), status, diagnosticMessage);

            // 🌟 FIXED PATH 1: If it's an HTTP failure code (like 500), trigger SMS alert here!
            if (!isHealthy && ("UP".equals(oldStatus) || oldStatus == null)) {
                System.out.println("DEBUG: Triggering HTTP Error Outage SMS for " + s.getName());
                List<AlertContact> contacts = contactRepo.findAll();
                String messageBody = String.format("🚨 ALERT: %s is DOWN! URL: %s. Failure Reason: %s", 
                        s.getName(), s.getUrl(), diagnosticMessage);
                
                for (AlertContact contact : contacts) {
                    smsService.sendSms(contact.getPhoneNumber(), messageBody);
                }
            }

        } catch (Exception e) {
            String status = "DOWN";
            
            // Cleanly classify typical network edge failures
            String failureReason;
            if (e instanceof SocketTimeoutException) {
                failureReason = "Connection Timeout (Exceeded 5000ms limitation limit)";
            } else if (e.getMessage() != null && e.getMessage().contains("PKIX path building failed")) {
                failureReason = "SSL Certificate Handshake Failed (Untrusted Security Profile)";
            } else {
                failureReason = e.getClass().getSimpleName() + ": " + e.getMessage();
            }

            logger.error("Service {} failed check. Reason: {}", s.getName(), failureReason);

            // 1. Database structural fallbacks
            s.setStatus(status);
            s.setResponseTime(null);
            s.setLastCheck(LocalDateTime.now());
            repo.save(s);

            // 2. Log exact tracking reason row
            ServiceLog log = new ServiceLog();
            log.setServiceId(s.getId());
            log.setStatus(status);
            log.setResponseTime(null);
            log.setLastCheck(LocalDateTime.now());
            log.setErrorMessage(failureReason);
            logRepo.save(log);
            
            // 🌟 PATH 2: Keeps your network crashes (timeouts, SSL blocks) alerting properly
            if ("UP".equals(oldStatus) || oldStatus == null) {
                System.out.println("DEBUG: Triggering Network Crash Outage SMS for " + s.getName());
                List<AlertContact> contacts = contactRepo.findAll();
                String messageBody = String.format("🚨 ALERT: %s is DOWN! URL: %s. Failure Reason: %s", 
                        s.getName(), s.getUrl(), failureReason);
                
                for (AlertContact contact : contacts) {
                    smsService.sendSms(contact.getPhoneNumber(), messageBody);
                }
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}