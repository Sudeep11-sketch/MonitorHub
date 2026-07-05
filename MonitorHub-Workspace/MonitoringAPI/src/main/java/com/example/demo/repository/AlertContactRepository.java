package com.example.demo.repository;

import com.example.demo.entity.AlertContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AlertContactRepository extends JpaRepository<AlertContact, Long> {
    // You can add custom methods here if needed, but JpaRepository.findAll() is sufficient
}