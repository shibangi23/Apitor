package com.monitor.dto;

import lombok.Data;

@Data
public class ApiRequest {
    private String name;
    private String url;
    private String method;
    private int expectedStatus;
}