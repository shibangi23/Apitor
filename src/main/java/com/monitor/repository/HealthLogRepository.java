package com.monitor.repository;

import com.monitor.entity.HealthLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthLogRepository extends JpaRepository<HealthLog, Long> {
}