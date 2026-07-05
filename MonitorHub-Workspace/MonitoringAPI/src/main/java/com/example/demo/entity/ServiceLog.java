package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_log")
@NoArgsConstructor // Required by JPA for instantiation
public class ServiceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The ID of the service that was checked (foreign key reference)
    private Long serviceId;

    // Status at the time of the check ("UP" or "DOWN")
    private String status;

    // Response time in milliseconds (can be null if status is DOWN)
    private Integer responseTime;

    // The timestamp when the check occurred
    private LocalDateTime lastCheck;

    // Added property to store human-readable diagnostic error text summaries
    @Column(name = "error_message", length = 500)
    private String errorMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(Integer responseTime) {
        this.responseTime = responseTime;
    }

    public LocalDateTime getLastCheck() {
        return lastCheck;
    }

    public void setLastCheck(LocalDateTime LastCheck) {
        this.lastCheck = LastCheck;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}