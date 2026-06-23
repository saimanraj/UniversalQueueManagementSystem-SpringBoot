package com.uqs.dto;

import com.uqs.entity.User;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be 2-100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    private String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Enter valid 10-digit phone number")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    private User.Role role = User.Role.CUSTOMER;

    // Vendor fields (optional - only if role is VENDOR)
    private String shopName;
    private String category;
    private String description;
    private String address;
    private Integer avgServiceTime = 5;
}
