package com.uqs.repository;

import com.uqs.entity.Queue;
import com.uqs.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {
    Optional<Queue> findByVendor(Vendor vendor);
    Optional<Queue> findByVendorId(Long vendorId);
    long countByIsActiveTrue();
}
