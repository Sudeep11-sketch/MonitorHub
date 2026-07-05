package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.AlertContact;
import com.example.demo.repository.AlertContactRepository;

import lombok.RequiredArgsConstructor;

// ... imports (AlertContact, AlertContactRepository, etc.)

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertContactController {

    private final AlertContactRepository contactRepository;

    public AlertContactController(AlertContactRepository contactRepository) {
    	this.contactRepository=contactRepository;
    }
    @PostMapping("/register")
    public AlertContact registerContact(@RequestBody AlertContact contact) {
        return contactRepository.save(contact);
    }
}