package com.uqs.controller;

import com.uqs.dto.QueueStatusDto;
import com.uqs.entity.User;
import com.uqs.service.QueueService;
import com.uqs.util.AuthUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST API for AJAX polling (queue status, live updates).
 * CSRF is disabled for /api/** in SecurityConfig.
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final AuthUtil authUtil;
    private final QueueService queueService;

    public ApiController(AuthUtil authUtil, QueueService queueService) {
        this.authUtil = authUtil;
        this.queueService = queueService;
    }

    /**
     * GET /api/queue/status/{vendorId}
     * Returns live queue status for the logged-in customer at a given vendor.
     */
    @GetMapping("/queue/status/{vendorId}")
    public ResponseEntity<?> getQueueStatus(@PathVariable Long vendorId) {
        User user = authUtil.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        QueueStatusDto status = queueService.getQueueStatus(user.getId(), vendorId);
        if (status == null) {
            return ResponseEntity.ok(Map.of(
                "found", false,
                "message", "No active token"
            ));
        }

        return ResponseEntity.ok(Map.of(
            "found", true,
            "tokenNo",       status.getTokenNo(),
            "status",        status.getStatus(),
            "peopleAhead",   status.getPeopleAhead(),
            "etaMinutes",    status.getEtaMinutes(),
            "currentToken",  status.getCurrentToken(),
            "queueActive",   status.getQueueActive(),
            "queuePaused",   status.getQueuePaused()
        ));
    }

    /**
     * GET /api/vendor/stats/{vendorId}
     * Returns live stats for vendor dashboard (waiting count, current token).
     */
    @GetMapping("/vendor/stats/{vendorId}")
    public ResponseEntity<?> getVendorStats(@PathVariable Long vendorId) {
        User user = authUtil.getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        long waiting = queueService.countWaiting(vendorId);
        var queueOpt = queueService.getQueueByVendorId(vendorId);

        return ResponseEntity.ok(Map.of(
            "waiting",      waiting,
            "currentToken", queueOpt.map(q -> q.getCurrentToken()).orElse(0),
            "isActive",     queueOpt.map(q -> q.getIsActive()).orElse(false),
            "isPaused",     queueOpt.map(q -> q.getIsPaused()).orElse(false)
        ));
    }
}
