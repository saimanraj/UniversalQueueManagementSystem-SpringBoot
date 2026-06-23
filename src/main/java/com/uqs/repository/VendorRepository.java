package com.uqs.repository;

import com.uqs.entity.Vendor;
import com.uqs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUser(User user);
    Optional<Vendor> findByUserId(Long userId);
    List<Vendor> findByApprovedTrue();
    List<Vendor> findByApprovedFalse();
    List<Vendor> findByCategory(String category);
    boolean existsByUser(User user);
}
