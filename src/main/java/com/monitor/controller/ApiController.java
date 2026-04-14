package com.monitor.controller;

import com.monitor.dto.ApiRequest;
import com.monitor.entity.Api;
import com.monitor.entity.HealthLog;
import com.monitor.repository.HealthLogRepository;
import com.monitor.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiService apiService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HealthLogRepository healthLogRepository;

    // ✅ Health check
    @GetMapping("/health")
    public String health() {
        return "API Health Monitor is running 🚀";
    }

    // ✅ Register API
    @PostMapping("/register")
    public Api register(@RequestBody ApiRequest request) {
        Api api = new Api();
        api.setName(request.getName());
        api.setUrl(request.getUrl());
        api.setMethod(request.getMethod());
        api.setExpectedStatus(request.getExpectedStatus());

        return apiService.register(api);
    }

    // ✅ Get all APIs
    @GetMapping("/list")
    public List<Api> getAll() {
        return apiService.getAll();
    }

    // ✅ Get latest status (from Redis ⚡)
    @GetMapping("/status/{id}")
    public String getStatus(@PathVariable Long id) {
        String status = redisTemplate.opsForValue().get("api_status_" + id);
        return (status != null) ? status : "No data yet";
    }

    // 🔥 NEW: Get all health logs (VERY IMPORTANT)
    @GetMapping("/logs")
    public List<HealthLog> getLogs() {
        return healthLogRepository.findAll();
    }

    @GetMapping("/logs/paged")
    public List<HealthLog> getLogsPaged(
            @RequestParam int page,
            @RequestParam int size) {

        return healthLogRepository
                .findAll(PageRequest.of(page, size))
                .getContent();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        apiService.delete(id);
        return "Deleted";
    }
}