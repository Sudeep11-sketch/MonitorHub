
package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "alert_contacts")
public class AlertContact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String phoneNumber;

    // --- 1. Constructors ---

    // No-Args Constructor (Required by JPA/Hibernate)
    public AlertContact() {
    }

    // All-Args Constructor (Optional, but useful)
    public AlertContact(Long id, String username, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    // --- 2. Getters (Required by HealthChecker for sending SMS) ---

    public Long getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }

    // This is the CRITICAL getter needed by HealthChecker.java
    public String getPhoneNumber() {
        return phoneNumber;
    }

    // --- 3. Setters (Required for form/database persistence) ---

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}