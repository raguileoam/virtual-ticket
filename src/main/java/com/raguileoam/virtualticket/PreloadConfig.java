package com.raguileoam.virtualticket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.raguileoam.virtualticket.model.Office;
import com.raguileoam.virtualticket.repositories.OfficeRepository;
import com.raguileoam.virtualticket.security.SecurityConfig;
import com.raguileoam.virtualticket.security.model.Account;
import com.raguileoam.virtualticket.security.model.ERole;
import com.raguileoam.virtualticket.security.repository.AccountRepository;

@Configuration
public class PreloadConfig {

    @Value("${app.user.email}")
    private String userEmail;

    @Value("${app.user.password}")
    private String userPassword;

    @Autowired
    OfficeRepository officeRepository;

    @Autowired
    AccountRepository accountRepository;

    @Bean
    public CommandLineRunner chargeOffice() {
        return (args -> {
            officeRepository.save(new Office());
        });
    }

    @Bean
    public CommandLineRunner chargeAccounts() {
        return (args -> {
            SecurityConfig securityConfig = new SecurityConfig();
            String password = securityConfig.encryptPassword(userPassword);
            Account account = new Account("user", userEmail, password);
            account.setRol(ERole.ADMIN);
            accountRepository.save(account);
        });
    }
}
