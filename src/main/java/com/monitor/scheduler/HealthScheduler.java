package com.monitor.scheduler;

import com.monitor.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HealthScheduler {

    @Autowired
    private ApiService apiService;

    @Scheduled(fixedRate = 10000) // every 10 sec
    public void runHealthCheck() {
        System.out.println("Running health check...");
        apiService.checkHealth();
    }
}