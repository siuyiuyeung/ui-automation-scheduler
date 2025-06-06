package com.automation.repository;

import com.automation.model.AutomationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AutomationConfigRepository extends JpaRepository<AutomationConfig, Long> {
    List<AutomationConfig> findByActiveTrue();
    List<AutomationConfig> findByNameContainingIgnoreCase(String name);
} 