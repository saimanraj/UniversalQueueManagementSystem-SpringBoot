package com.uqs.controller;

import com.uqs.dto.DashboardDto;
import com.uqs.dto.VendorQueueInfo;
import com.uqs.entity.*;
import com.uqs.service.*;
import com.uqs.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AuthUtil authUtil;
    private final UserService userService;
    private final VendorService vendorService;
    private final QueueService queueService;

    public AdminController(AuthUtil authUtil,
                           UserService userService,
                           VendorService vendorService,
                           QueueService queueService) {
        this.authUtil = authUtil;
        this.userService = userService;
        this.vendorService = vendorService;
        this.queueService = queueService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = authUtil.getCurrentUser();

        DashboardDto stats = DashboardDto.builder()
            .totalUsers(userService.countAll())
            .totalVendors(vendorService.countAll())
            .pendingVendors(vendorService.countPending())
            .activeQueues(queueService.countActiveQueues())
            .build();

        List<Vendor> pendingVendors = vendorService.getPendingVendors();

        model.addAttribute("user", user);
        model.addAttribute("stats", stats);
        model.addAttribute("pendingVendors", pendingVendors);
        return "admin/dashboard";
    }

    @GetMapping("/vendors")
    public String vendors(Model model) {
        model.addAttribute("user", authUtil.getCurrentUser());
        model.addAttribute("allVendors", vendorService.getAllVendors());
        model.addAttribute("pendingVendors", vendorService.getPendingVendors());
        return "admin/vendors";
    }

    @PostMapping("/vendors/{id}/approve")
    public String approveVendor(@PathVariable Long id, RedirectAttributes ra) {
        vendorService.approveVendor(id);
        ra.addFlashAttribute("success", "Vendor approved successfully!");
        return "redirect:/admin/vendors";
    }

    @PostMapping("/vendors/{id}/reject")
    public String rejectVendor(@PathVariable Long id, RedirectAttributes ra) {
        vendorService.rejectVendor(id);
        ra.addFlashAttribute("success", "Vendor rejected.");
        return "redirect:/admin/vendors";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("user", authUtil.getCurrentUser());
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @GetMapping("/queues")
    public String queues(Model model) {
        List<Vendor> vendors = vendorService.getAllApprovedVendors();

        List<VendorQueueInfo> queueInfos = vendors.stream().map(vendor -> {
            var queueOpt = queueService.getQueueByVendorId(vendor.getId());
            return VendorQueueInfo.builder()
                .vendorId(vendor.getId())
                .shopName(vendor.getShopName())
                .category(vendor.getCategory())
                .isActive(queueOpt.map(q -> q.getIsActive()).orElse(false))
                .isPaused(queueOpt.map(q -> q.getIsPaused()).orElse(false))
                .currentToken(queueOpt.map(q -> q.getCurrentToken()).orElse(0))
                .waitingCount(queueService.countWaiting(vendor.getId()))
                .avgServiceTime(vendor.getAvgServiceTime())
                .build();
        }).collect(Collectors.toList());

        model.addAttribute("user", authUtil.getCurrentUser());
        model.addAttribute("queueInfos", queueInfos);
        return "admin/queues";
    }
}
