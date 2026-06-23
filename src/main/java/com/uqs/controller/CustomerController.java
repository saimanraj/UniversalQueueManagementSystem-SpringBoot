package com.uqs.controller;

import com.uqs.dto.QueueStatusDto;
import com.uqs.entity.*;
import com.uqs.service.*;
import com.uqs.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final AuthUtil authUtil;
    private final VendorService vendorService;
    private final QueueService queueService;

    public CustomerController(AuthUtil authUtil,
                               VendorService vendorService,
                               QueueService queueService) {
        this.authUtil = authUtil;
        this.vendorService = vendorService;
        this.queueService = queueService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = authUtil.getCurrentUser();
        model.addAttribute("user", user);
        List<Token> history = queueService.getUserTokenHistory(user.getId());
        model.addAttribute("tokenHistory", history);

        // Pre-compute counts (Thymeleaf doesn't support Java Stream API)
        long waiting   = history.stream().filter(t -> t.getStatus() == Token.TokenStatus.WAITING).count();
        long serving   = history.stream().filter(t -> t.getStatus() == Token.TokenStatus.SERVING).count();
        long served    = history.stream().filter(t -> t.getStatus() == Token.TokenStatus.SERVED).count();
        long cancelled = history.stream().filter(t -> t.getStatus() == Token.TokenStatus.CANCELLED).count();
        model.addAttribute("countWaiting",   waiting + serving);  // active = waiting + serving
        model.addAttribute("countServed",    served);
        model.addAttribute("countCancelled", cancelled);
        return "customer/dashboard";
    }

    @GetMapping("/vendors")
    public String listVendors(Model model) {
        List<Vendor> vendors = vendorService.getAllApprovedVendors();
        model.addAttribute("vendors", vendors);
        model.addAttribute("user", authUtil.getCurrentUser());
        return "customer/vendors";
    }

    @GetMapping("/join/{vendorId}")
    public String joinQueuePage(@PathVariable Long vendorId, Model model) {
        User user = authUtil.getCurrentUser();
        Vendor vendor = vendorService.findById(vendorId)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));
        Queue queue = queueService.getQueueByVendorId(vendorId).orElse(null);
        long waiting = queueService.countWaiting(vendorId);

        model.addAttribute("vendor", vendor);
        model.addAttribute("queue", queue);
        model.addAttribute("waiting", waiting);
        model.addAttribute("user", user);

        // Check if user already has a token
        QueueStatusDto status = queueService.getQueueStatus(user.getId(), vendorId);
        model.addAttribute("existingToken", status);

        return "customer/join";
    }

    @PostMapping("/join/{vendorId}")
    public String joinQueue(@PathVariable Long vendorId,
                            RedirectAttributes redirectAttrs) {
        User user = authUtil.getCurrentUser();
        try {
            Token token = queueService.joinQueue(user.getId(), vendorId, user);
            redirectAttrs.addFlashAttribute("success",
                "Joined queue! Your token number is: #" + token.getTokenNo());
            return "redirect:/customer/track/" + vendorId;
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/customer/join/" + vendorId;
        }
    }

    @GetMapping("/track/{vendorId}")
    public String trackQueue(@PathVariable Long vendorId, Model model) {
        User user = authUtil.getCurrentUser();
        QueueStatusDto status = queueService.getQueueStatus(user.getId(), vendorId);
        Vendor vendor = vendorService.findById(vendorId).orElse(null);
        Queue queue = queueService.getQueueByVendorId(vendorId).orElse(null);

        model.addAttribute("status", status);
        model.addAttribute("vendor", vendor);
        model.addAttribute("queue", queue);
        model.addAttribute("user", user);
        return "customer/track";
    }

    @PostMapping("/cancel/{tokenId}")
    public String cancelToken(@PathVariable Long tokenId,
                              RedirectAttributes redirectAttrs) {
        User user = authUtil.getCurrentUser();
        try {
            queueService.cancelToken(tokenId, user.getId());
            redirectAttrs.addFlashAttribute("success", "Token cancelled successfully");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/dashboard";
    }
}
