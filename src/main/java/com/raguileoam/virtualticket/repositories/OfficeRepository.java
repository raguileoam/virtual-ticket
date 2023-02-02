package com.raguileoam.virtualticket.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.raguileoam.virtualticket.model.Office;

public interface OfficeRepository extends JpaRepository<Office, Long>{
    
}
