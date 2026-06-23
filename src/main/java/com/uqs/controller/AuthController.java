package com.uqs.controller;

import com.uqs.dto.RegisterDto;
import com.uqs.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String expired,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password");
        if (logout != null) model.addAttribute("message", "You have been logged out");
        if (expired != null) model.addAttribute("error", "Session expired. Please login again.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(@RequestParam(defaultValue = "customer") String type, Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        model.addAttribute("type", type);
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam(defaultValue = "customer") String type,
                           @Valid @ModelAttribute("registerDto") RegisterDto dto,
                           BindingResult result,
                           RedirectAttributes redirectAttrs,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("type", type);
            return "auth/register";
        }

        try {
            if ("vendor".equals(type)) {
                if (dto.getShopName() == null || dto.getShopName().isBlank()) {
                    model.addAttribute("error", "Shop name is required for vendor registration");
                    model.addAttribute("type", type);
                    return "auth/register";
                }
                userService.registerVendor(dto);
                redirectAttrs.addFlashAttribute("message",
                    "Vendor registered! Await admin approval before you can use the system.");
            } else {
                userService.registerCustomer(dto);
                redirectAttrs.addFlashAttribute("message", "Registration successful! Please login.");
            }
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("type", type);
            return "auth/register";
        }
    }
}
