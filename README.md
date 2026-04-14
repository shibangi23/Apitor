# 🚀 Apitor - API Health Monitoring System

A backend system built using Spring Boot to monitor the health of APIs in real-time, detect failures, and provide fast status retrieval using Redis caching.

---

## 📌 Overview

Apitor continuously monitors registered APIs by performing scheduled health checks, tracking response times, and identifying failures. It uses Redis for fast access to the latest API status and implements retry + alert mechanisms for reliability.

---

## ✨ Features

- Register and manage APIs
- Scheduled health checks (every 10 seconds)
- Retry mechanism for improved reliability
- Response time tracking
- Failure detection (alerts after 3 consecutive failures)
- Redis caching for low-latency status lookup
- Health logs storage and retrieval
- Clean layered architecture (Controller, Service, Repository)
- Global exception handling

---

## 🛠️ Tech Stack

- Java 17  
- Spring Boot  
- Spring Data JPA  
- H2 Database  
- Redis (Memurai)  
- REST APIs  
- Maven  

---

## 🏗️ Architecture

Controller → Service → Repository → Database  
                             ↓  
                         Redis Cache  

---

## ⚙️ How It Works

1. APIs are registered via REST endpoint  
2. A scheduler runs every 10 seconds  
3. Each API is called and validated  
4. Response status and time are recorded  
5. Failures are tracked using Redis  
6. Alerts are triggered after 3 consecutive failures  
7. Latest API status is cached in Redis  

---

## 🔌 API Endpoints

POST /api/register → Register API  
GET /api/list → Get all APIs  
GET /api/status/{id} → Get API status (from Redis)  
GET /api/logs → Get health logs  
DELETE /api/{id} → Delete API  

---

## 🧪 Sample Request

{
  "name": "Google",
  "url": "https://www.google.com",
  "method": "GET",
  "expectedStatus": 200
}

---

## 🚨 Alert Logic

- Tracks consecutive failures using Redis  
- If an API fails 3 times continuously, an alert is triggered  
- Failure count resets when API becomes healthy  

---

## ⚡ Redis Usage

- api_status_{id} → stores latest API status (UP/DOWN)  
- api_fail_count_{id} → tracks consecutive failures  

---

## ▶️ Running the Project

1. Clone the repository  

2. Start Redis (Memurai)  
Ensure Redis is running on localhost:6379  

3. Run the application  
.\mvnw.cmd spring-boot:run  

4. Access APIs  
http://localhost:8080/api/...  

---

## 📈 Future Improvements

- Email/Slack alerts  
- Docker containerization  
- Dashboard UI  
- Metrics & monitoring  

---
