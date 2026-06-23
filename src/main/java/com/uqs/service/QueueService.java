package com.uqs.service;

import com.uqs.dto.QueueStatusDto;
import com.uqs.entity.*;
import com.uqs.entity.Token.TokenStatus;
import com.uqs.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QueueService {

    private final QueueRepository queueRepository;
    private final TokenRepository tokenRepository;
    private final VendorRepository vendorRepository;

    public QueueService(QueueRepository queueRepository,
                        TokenRepository tokenRepository,
                        VendorRepository vendorRepository) {
        this.queueRepository = queueRepository;
        this.tokenRepository = tokenRepository;
        this.vendorRepository = vendorRepository;
    }

    // ==================== VENDOR QUEUE MANAGEMENT ====================

    @Transactional
    public Queue openQueue(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));

        Queue queue = queueRepository.findByVendorId(vendorId)
            .orElseGet(() -> Queue.builder().vendor(vendor).currentToken(0).build());

        queue.setIsActive(true);
        queue.setIsPaused(false);
        queue.setOpenedAt(LocalDateTime.now());
        return queueRepository.save(queue);
    }

    @Transactional
    public Queue pauseQueue(Long vendorId) {
        Queue queue = queueRepository.findByVendorId(vendorId)
            .orElseThrow(() -> new RuntimeException("Queue not found"));
        queue.setIsPaused(true);
        return queueRepository.save(queue);
    }

    @Transactional
    public Queue resumeQueue(Long vendorId) {
        Queue queue = queueRepository.findByVendorId(vendorId)
            .orElseThrow(() -> new RuntimeException("Queue not found"));
        queue.setIsPaused(false);
        return queueRepository.save(queue);
    }

    @Transactional
    public Queue closeQueue(Long vendorId) {
        Queue queue = queueRepository.findByVendorId(vendorId)
            .orElseThrow(() -> new RuntimeException("Queue not found"));
        queue.setIsActive(false);
        queue.setIsPaused(false);
        return queueRepository.save(queue);
    }

    @Transactional
    public Token callNextToken(Long vendorId) {
        Queue queue = queueRepository.findByVendorId(vendorId)
            .orElseThrow(() -> new RuntimeException("Queue not found"));

        // Mark current SERVING token as SERVED
        tokenRepository.findByVendorIdAndStatus(vendorId, TokenStatus.SERVING)
            .ifPresent(t -> {
                t.setStatus(TokenStatus.SERVED);
                t.setServedAt(LocalDateTime.now());
                tokenRepository.save(t);
            });

        // Get next waiting token
        List<Token> waitingTokens = tokenRepository.findWaitingTokensByVendor(vendorId);
        if (waitingTokens.isEmpty()) {
            return null;
        }

        Token next = waitingTokens.get(0);
        next.setStatus(TokenStatus.SERVING);
        tokenRepository.save(next);

        queue.setCurrentToken(next.getTokenNo());
        queue.setLastCalledAt(LocalDateTime.now());
        queueRepository.save(queue);

        return next;
    }

    @Transactional
    public Token markTokenServed(Long tokenId) {
        Token token = tokenRepository.findById(tokenId)
            .orElseThrow(() -> new RuntimeException("Token not found"));
        token.setStatus(TokenStatus.SERVED);
        token.setServedAt(LocalDateTime.now());
        return tokenRepository.save(token);
    }

    // ==================== CUSTOMER QUEUE ACTIONS ====================

    @Transactional
    public Token joinQueue(Long userId, Long vendorId, User user) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));

        if (!vendor.getApproved()) {
            throw new RuntimeException("This vendor is not approved");
        }

        Queue queue = queueRepository.findByVendorId(vendorId)
            .orElseThrow(() -> new RuntimeException("Queue not found"));

        if (!queue.getIsActive()) {
            throw new RuntimeException("Queue is not active");
        }
        if (queue.getIsPaused()) {
            throw new RuntimeException("Queue is currently paused");
        }

        // Check if user already has an active token
        Optional<Token> existing = tokenRepository.findActiveTokenByUserAndVendor(userId, vendorId);
        if (existing.isPresent()) {
            throw new RuntimeException("You already have an active token for this vendor");
        }

        Integer nextNo = tokenRepository.getNextTokenNo(vendorId);

        Token token = Token.builder()
            .user(user)
            .vendor(vendor)
            .tokenNo(nextNo)
            .status(TokenStatus.WAITING)
            .build();

        return tokenRepository.save(token);
    }

    @Transactional
    public void cancelToken(Long tokenId, Long userId) {
        Token token = tokenRepository.findById(tokenId)
            .orElseThrow(() -> new RuntimeException("Token not found"));

        if (!token.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized action");
        }
        if (token.getStatus() == TokenStatus.SERVED) {
            throw new RuntimeException("Cannot cancel a served token");
        }

        token.setStatus(TokenStatus.CANCELLED);
        tokenRepository.save(token);
    }

    // ==================== STATUS / INFO ====================

    public QueueStatusDto getQueueStatus(Long userId, Long vendorId) {
        Optional<Token> tokenOpt = tokenRepository.findActiveTokenByUserAndVendor(userId, vendorId);
        Queue queue = queueRepository.findByVendorId(vendorId).orElse(null);
        Vendor vendor = vendorRepository.findById(vendorId).orElse(null);

        if (tokenOpt.isEmpty() || queue == null || vendor == null) {
            return null;
        }

        Token token = tokenOpt.get();
        Long ahead = tokenRepository.countAhead(vendorId, token.getTokenNo());

        return QueueStatusDto.builder()
            .tokenId(token.getId())
            .tokenNo(token.getTokenNo())
            .status(token.getStatus().name())
            .peopleAhead(ahead)
            .avgServiceTime(vendor.getAvgServiceTime())
            .etaMinutes(ahead * vendor.getAvgServiceTime())
            .vendorName(vendor.getShopName())
            .vendorId(vendorId)
            .queueActive(queue.getIsActive())
            .queuePaused(queue.getIsPaused())
            .currentToken(queue.getCurrentToken())
            .build();
    }

    public List<Token> getTokensForVendor(Long vendorId) {
        return tokenRepository.findByVendorIdOrderByTokenNoAsc(vendorId);
    }

    public List<Token> getWaitingTokens(Long vendorId) {
        return tokenRepository.findByVendorIdAndStatusOrderByTokenNoAsc(vendorId, TokenStatus.WAITING);
    }

    public List<Token> getUserTokenHistory(Long userId) {
        return tokenRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<Queue> getQueueByVendorId(Long vendorId) {
        return queueRepository.findByVendorId(vendorId);
    }

    public long countActiveQueues() {
        return queueRepository.countByIsActiveTrue();
    }

    public long countWaiting(Long vendorId) {
        return tokenRepository.countByVendorIdAndStatus(vendorId, TokenStatus.WAITING);
    }
}
