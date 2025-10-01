package com.example.final_projects;

import com.example.final_projects.support.MailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FinalProjectsApplicationTests {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MailService mailService() {
            return Mockito.mock(MailService.class);
        }
    }

    @Test
    void contextLoads() {
    }
}
