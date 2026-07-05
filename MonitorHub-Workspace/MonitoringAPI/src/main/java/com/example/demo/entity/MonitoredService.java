package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monitored_service")
public class MonitoredService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;
    private String status;
    private Integer responseTime;
    private LocalDateTime LastCheck;

    // Added to support dynamic user-configured check polling cycles (in seconds)
    @Column(name = "check_interval", nullable = false)
    private Integer checkInterval = 60; 

    // 🌟 NEW: Track ownership so fresh accounts don't load other people's cards
    @Column(name = "user_email")
    private String userEmail;

    // --- Constructors ---

    public MonitoredService() {
    }

    public MonitoredService(Long id, String name, String url, String status, Integer responseTime, LocalDateTime LastCheck, Integer checkInterval, String userEmail) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.status = status;
        this.responseTime = responseTime;
        this.LastCheck = LastCheck;
        this.checkInterval = checkInterval;
        this.userEmail = userEmail;
    }
    
    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public LocalDateTime getLastCheck() {
        return LastCheck;
    }

    public Integer getCheckInterval() {
        return checkInterval;
    }

    public String getUserEmail() {
        return userEmail;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }

    public void setLastCheck(LocalDateTime LastCheck) {
        this.LastCheck = LastCheck;
    }

    public void setCheckInterval(Integer checkInterval) {
        if (checkInterval != null) {
            this.checkInterval = checkInterval;
        }
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}