package com.example.final_projects.service;

import com.example.final_projects.entity.UserTemplateRequestFailureLog;
import com.example.final_projects.repository.UserTemplateRequestFailureLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FailureLogService {
    private final UserTemplateRequestFailureLogRepository failureLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailureLog(
            Long userTemplateRequestId,
            String errorCode,
            String errorDetail,
            Integer attemptNumber,
            String userAgent,
            String clientIp,
            Integer httpStatusCode,
            Long responseTimeMs
    ) {
        UserTemplateRequestFailureLog failureLog = UserTemplateRequestFailureLog.builder()
                .userTemplateRequestId(userTemplateRequestId)
                .errorCode(errorCode)
                .errorDetail(errorDetail)
                .attemptNumber(attemptNumber)
                .userAgent(userAgent)
                .clientIp(clientIp)
                .httpStatusCode(httpStatusCode)
                .responseTimeMs(responseTimeMs != null ? responseTimeMs.intValue() : null)
                // requestWaitTimeMs, requestReceivedAt 등은 이후 추가
                .build();
        failureLogRepository.save(failureLog);
    }
}
