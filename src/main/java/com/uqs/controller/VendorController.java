package com.uqs.controller;

import com.uqs.entity.*;
import com.uqs.service.*;
import com.uqs.util.AuthUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/vendor")
public class VendorController {

    private final AuthUtil authUtil;
    private final VendorService vendorService;
    private final QueueService queueService;

    public VendorController(AuthUtil authUtil,
                             VendorService vendorService,
                             QueueService queueService) {
        this.authUtil = authUtil;
        this.vendorService = vendorService;
        this.queueService = queueService;
    }

    private Vendor getVendorOrThrow(User user) {
        return vendorService.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Vendor profile not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User user = authUtil.getCurrentUser();
        Vendor vendor = getVendorOrThrow(user);
        Queue queue = queueService.getQueueByVendorId(vendor.getId()).orElse(null);
        List<Token> tokens = queueService.getTokensForVendor(vendor.getId());
        long waiting = queueService.countWaiting(vendor.getId());

        model.addAttribute("user", user);
        model.addAttribute("vendor", vendor);
        model.addAttribute("queue", queue);
        model.addAttribute("tokens", tokens);
        model.addAttribute("waiting", waiting);
        return "vendor/dashboard";
    }

    @PostMapping("/queue/open")
    public String openQueue(RedirectAttributes ra) {
        User user = authUtil.getCurrentUser();
        Vendor vendor = getVendorOrThrow(user);
        queueService.openQueue(vendor.getId());
        ra.addFlashAttribute("success", "Queue opened successfully!");
        return "redirect:/vendor/dashboard";
    }

    @PostMapping("/queue/pause")
    public String pauseQueue(RedirectAttributes ra) {
        User user = authUtil.getCurrentUser();
        Vendor vendor = getVendorOrThrow(user);
        queueService.pauseQueue(vendor.getId());
        ra.addFlashAttribute("success", "Queue paused.");
        return "redirect:/vendor/dashboard";
    }

    @PostMapping("/queue/resume")
    public String resumeQueue(RedirectAttributes ra) {
        User user = authUtil.getCurrentUser();
        Vendor vendor = getVendorOrThrow(user);
        queueService.resumeQueue(vendor.getId());
        ra.addFlashAttribute("success", "Queue resumed!");
        return "redirect:/vendor/dashboard";
    }

    @PostMapping("/queue/close")
    public String closeQueue(RedirectAttributes ra) {
        User user = authUtil.getCurrentUser();
        Vendor vendor = getVendorOrThrow(user);
        queueService.closeQueue(vendor.getId());
        ra.addFlashAttribute("success", "Queue closed.");
        return "redirect:/vendor/dashboard";
    }

    @PostMapping("/queue/next")
    public String callNext(RedirectAttributes ra) {
        User user = authUtil.getCurrentUser();
        Vendor vendor = getVendorOrThrow(user);
        Token next = queueService.callNextToken(vendor.getId());
        if (next != null) {
            ra.addFlashAttribute("success", "Now calling Token #" + next.getTokenNo()
                + " - " + next.getUser().getName());
        } else {
            ra.addFlashAttribute("info", "No more customers waiting.");
        }
        return "redirect:/vendor/dashboard";
    }

    @PostMapping("/token/{tokenId}/served")
    public String markServed(@PathVariable Long tokenId, RedirectAttributes ra) {
        queueService.markTokenServed(tokenId);
        ra.addFlashAttribute("success", "Token marked as served.");
        return "redirect:/vendor/dashboard";
    }

    @PostMapping("/settings/service-time")
    public String updateServiceTime(@RequestParam Integer minutes, RedirectAttributes ra) {
        User user = authUtil.getCurrentUser();
        Vendor vendor = getVendorOrThrow(user);
        vendorService.updateAvgServiceTime(vendor.getId(), minutes);
        ra.addFlashAttribute("success", "Average service time updated to " + minutes + " minutes.");
        return "redirect:/vendor/dashboard";
    }
}
