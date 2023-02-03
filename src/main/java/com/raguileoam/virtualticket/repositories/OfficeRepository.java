package com.raguileoam.virtualticket.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.raguileoam.virtualticket.model.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, Long>{
    
}
