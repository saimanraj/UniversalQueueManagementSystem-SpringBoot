package com.uqs.config;

import com.uqs.entity.User;
import com.uqs.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Runs once at startup.
 * Ensures the default Admin account exists with the correct BCrypt password.
 * This is the single source of truth — no need to rely on schema.sql hash.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createAdminIfMissing();
    }

    private void createAdminIfMissing() {
        String adminEmail    = "admin@uqs.com";
        String adminPassword = "admin123";

        userRepository.findByEmail(adminEmail).ifPresentOrElse(
            existing -> {
                // Admin exists — verify password is correct, re-hash if needed
                if (!passwordEncoder.matches(adminPassword, existing.getPassword())) {
                    log.warn("Admin password hash mismatch — re-hashing now...");
                    existing.setPassword(passwordEncoder.encode(adminPassword));
                    userRepository.save(existing);
                    log.info("Admin password re-hashed successfully.");
                } else {
                    log.info("Admin account OK: {}", adminEmail);
                }
            },
            () -> {
                // Admin doesn't exist — create fresh
                User admin = User.builder()
                    .name("Admin")
                    .email(adminEmail)
                    .phone("9999999999")
                    .password(passwordEncoder.encode(adminPassword))
                    .role(User.Role.ADMIN)
                    .build();
                userRepository.save(admin);
                log.info("Default admin created: {} / {}", adminEmail, adminPassword);
            }
        );
    }
}
