package com.example.final_projects.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DbHealthController {

    private final JdbcTemplate jdbcTemplate;

    // 수동 생성자 주입
    public DbHealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health/db")
    public Map<String, Object> ping() {
        Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return Map.of("ok", one != null && one == 1);
    }
}
