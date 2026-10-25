package com.ewaste.repository;

import com.ewaste.model.User;
import com.ewaste.model.Collector;
import com.ewaste.model.WasteRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WasteRequestRepository extends JpaRepository<WasteRequest, Long> {

    List<WasteRequest> findByUserOrderByCreatedAtDesc(User user);

    List<WasteRequest> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);

    boolean existsByUserAndCategoryAndStatus(
            User user,
            String category,
            String status
    );

    // Get all pickup requests assigned to a particular collector
    List<WasteRequest> findByCollectorOrderByCreatedAtDesc(Collector collector);
}