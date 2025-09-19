package com.example.final_projects.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_template_request_failure_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTemplateRequestFailureLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_template_request_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserTemplateRequest userTemplateRequest;

    @Column(name = "error_code", length = 10)
    private String errorCode;

    @Column(name = "error_detail", columnDefinition = "TEXT")
    private String errorDetail;

    @Column(name = "attempt_number")
    private Integer attemptNumber;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "client_ip", length = 64)
    private String clientIp;

    @Column(name = "http_status_code")
    private Integer httpStatusCode;

    @Column(name = "request_wait_time_ms")
    private Integer requestWaitTimeMs;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Column(name = "request_received_at")
    private LocalDateTime requestReceivedAt;

    @Column(name = "response_returned_at")
    private LocalDateTime responseReturnedAt;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
