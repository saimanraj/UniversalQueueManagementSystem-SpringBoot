package com.uqs.service;

import com.uqs.dto.RegisterDto;
import com.uqs.entity.User;
import com.uqs.entity.Vendor;
import com.uqs.entity.Queue;
import com.uqs.repository.UserRepository;
import com.uqs.repository.VendorRepository;
import com.uqs.repository.QueueRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;
    private final QueueRepository queueRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       VendorRepository vendorRepository,
                       QueueRepository queueRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.queueRepository = queueRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerCustomer(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = User.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .password(passwordEncoder.encode(dto.getPassword()))
            .role(User.Role.CUSTOMER)
            .build();

        return userRepository.save(user);
    }

    @Transactional
    public User registerVendor(RegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        User user = User.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .password(passwordEncoder.encode(dto.getPassword()))
            .role(User.Role.VENDOR)
            .build();

        user = userRepository.save(user);

        Vendor vendor = Vendor.builder()
            .user(user)
            .shopName(dto.getShopName())
            .category(dto.getCategory())
            .description(dto.getDescription())
            .address(dto.getAddress())
            .avgServiceTime(dto.getAvgServiceTime() != null ? dto.getAvgServiceTime() : 5)
            .approved(false)
            .build();

        vendor = vendorRepository.save(vendor);

        // Create a queue entry for this vendor
        Queue queue = Queue.builder()
            .vendor(vendor)
            .currentToken(0)
            .isActive(false)
            .isPaused(false)
            .build();

        queueRepository.save(queue);

        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public long countByRole(User.Role role) {
        return userRepository.countByRole(role);
    }

    public long countAll() {
        return userRepository.count();
    }
}
