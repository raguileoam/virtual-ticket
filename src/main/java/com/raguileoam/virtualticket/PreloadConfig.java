package com.raguileoam.virtualticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.raguileoam.virtualticket.model.Office;
import com.raguileoam.virtualticket.repositories.OfficeRepository;

@Configuration
public class PreloadConfig {

    @Autowired
    OfficeRepository officeRepository;

    @Bean
    public CommandLineRunner chargeOffice() {
        return (args -> {
            officeRepository.save(new Office());
        });
    }
}
