package com.uqs.repository;

import com.uqs.entity.Token;
import com.uqs.entity.Token.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    // Get next token number for a vendor
    @Query("SELECT COALESCE(MAX(t.tokenNo), 0) + 1 FROM Token t WHERE t.vendor.id = :vendorId")
    Integer getNextTokenNo(@Param("vendorId") Long vendorId);

    // Count people waiting ahead of this token
    @Query("SELECT COUNT(t) FROM Token t WHERE t.vendor.id = :vendorId AND t.status = 'WAITING' AND t.tokenNo < :tokenNo")
    Long countAhead(@Param("vendorId") Long vendorId, @Param("tokenNo") Integer tokenNo);

    // Find active token for user at a vendor
    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.vendor.id = :vendorId AND t.status IN ('WAITING', 'SERVING')")
    Optional<Token> findActiveTokenByUserAndVendor(@Param("userId") Long userId, @Param("vendorId") Long vendorId);

    // Get all tokens for a vendor ordered by token number
    List<Token> findByVendorIdOrderByTokenNoAsc(Long vendorId);

    // Get waiting tokens for vendor
    List<Token> findByVendorIdAndStatusOrderByTokenNoAsc(Long vendorId, TokenStatus status);

    // Get all tokens for a user
    List<Token> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Count waiting tokens for a vendor
    long countByVendorIdAndStatus(Long vendorId, TokenStatus status);

    // Get next token to be called
    @Query("SELECT t FROM Token t WHERE t.vendor.id = :vendorId AND t.status = 'WAITING' ORDER BY t.tokenNo ASC")
    List<Token> findWaitingTokensByVendor(@Param("vendorId") Long vendorId);

    // Find currently serving token
    Optional<Token> findByVendorIdAndStatus(Long vendorId, TokenStatus status);
}
