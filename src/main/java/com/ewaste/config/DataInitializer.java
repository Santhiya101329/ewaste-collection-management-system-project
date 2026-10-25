package com.ewaste.config;

import com.ewaste.model.User;
import com.ewaste.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        String adminEmail = "admin@ewaste.com";

        if (userRepository.findByEmail(adminEmail) == null) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail(adminEmail);
            admin.setPassword("admin123");
            admin.setPhone("0000000000");
            admin.setAddress("Head Office");
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("✅ Admin account created: " + adminEmail);
        } else {
            System.out.println("ℹ️ Admin account already exists: " + adminEmail);
        }
    }
}