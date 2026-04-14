package com.monitor.service;

import com.monitor.entity.Api;
import com.monitor.entity.HealthLog;
import com.monitor.repository.ApiRepository;
import com.monitor.repository.HealthLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    private HealthLogRepository healthLogRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private RestTemplate restTemplate = createRestTemplate();

    // ✅ Register API
    public Api register(Api api) {
        return apiRepository.save(api);
    }

    // ✅ Get all APIs
    public List<Api> getAll() {
        return apiRepository.findAll();
    }

    // 🚀 MAIN HEALTH CHECK METHOD
    public void checkHealth() {
        logger.info("Running health check...");

        List<Api> apis = apiRepository.findAll();

        for (Api api : apis) {
            long start = System.currentTimeMillis();

            boolean isUp = isApiUp(api);
            long responseTime = System.currentTimeMillis() - start;

            if (isUp) {
                resetFailureCount(api.getId());
                saveLog(api.getId(), "UP", responseTime);
                logger.info("API {} is UP", api.getId());
            } else {
                incrementFailureCount(api.getId());
                saveLog(api.getId(), "DOWN", responseTime);
                logger.warn("API {} is DOWN", api.getId());
            }
        }
    }

    // 🔁 RETRY LOGIC
    private boolean isApiUp(Api api) {
        int retries = 2;

        for (int i = 0; i <= retries; i++) {
            try {
                var response = restTemplate.getForEntity(api.getUrl(), String.class);

                if (response.getStatusCode().value() == api.getExpectedStatus()) {
                    return true;
                }

            } catch (Exception ignored) {
            }
        }

        return false;
    }

    // 💾 SAVE LOG + CACHE STATUS
    private void saveLog(Long apiId, String status, long responseTime) {
        HealthLog log = new HealthLog();
        log.setApiId(apiId);
        log.setStatus(status);
        log.setResponseTime(responseTime);
        log.setTimestamp(LocalDateTime.now());

        healthLogRepository.save(log);

        // 🔥 Store latest status in Redis
        redisTemplate.opsForValue().set("api_status_" + apiId, status);
    }

    // 📉 FAILURE COUNT
    private void incrementFailureCount(Long apiId) {
        String key = "api_fail_count_" + apiId;

        String countStr = redisTemplate.opsForValue().get(key);
        int count = (countStr != null) ? Integer.parseInt(countStr) : 0;

        count++;
        redisTemplate.opsForValue().set(key, String.valueOf(count));

        logger.warn("API {} failure count: {}", apiId, count);

        if (count >= 3) {
            triggerAlert(apiId, count);
        }
    }

    // 🔄 RESET FAILURE COUNT
    private void resetFailureCount(Long apiId) {
        String key = "api_fail_count_" + apiId;
        redisTemplate.delete(key);
    }

    // 🚨 ALERT SYSTEM
    private void triggerAlert(Long apiId, int count) {
        logger.error("🚨 ALERT: API {} is DOWN {} times consecutively!", apiId, count);
    }

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);

        return new RestTemplate(factory);
    }

    public void delete(Long id) {
        apiRepository.deleteById(id);
    }

}
