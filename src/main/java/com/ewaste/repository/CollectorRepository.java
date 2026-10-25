package com.ewaste.repository;

import com.ewaste.model.Collector;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CollectorRepository extends JpaRepository<Collector, Long> {
    List<Collector> findByAvailableTrue();
}