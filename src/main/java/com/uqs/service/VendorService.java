package com.uqs.service;

import com.uqs.entity.Vendor;
import com.uqs.entity.User;
import com.uqs.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class VendorService {

    private final VendorRepository vendorRepository;

    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    public List<Vendor> getAllApprovedVendors() {
        return vendorRepository.findByApprovedTrue();
    }

    public List<Vendor> getPendingVendors() {
        return vendorRepository.findByApprovedFalse();
    }

    public List<Vendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public Optional<Vendor> findById(Long id) {
        return vendorRepository.findById(id);
    }

    public Optional<Vendor> findByUser(User user) {
        return vendorRepository.findByUser(user);
    }

    public Optional<Vendor> findByUserId(Long userId) {
        return vendorRepository.findByUserId(userId);
    }

    @Transactional
    public Vendor approveVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setApproved(true);
        return vendorRepository.save(vendor);
    }

    @Transactional
    public void rejectVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setApproved(false);
        vendorRepository.save(vendor);
    }

    @Transactional
    public Vendor updateAvgServiceTime(Long vendorId, Integer minutes) {
        Vendor vendor = vendorRepository.findById(vendorId)
            .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setAvgServiceTime(minutes);
        return vendorRepository.save(vendor);
    }

    public long countAll() {
        return vendorRepository.count();
    }

    public long countPending() {
        return vendorRepository.findByApprovedFalse().size();
    }
}
