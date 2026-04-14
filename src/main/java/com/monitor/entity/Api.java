package com.monitor.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Api {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;
    private String method;
    private int expectedStatus;
}