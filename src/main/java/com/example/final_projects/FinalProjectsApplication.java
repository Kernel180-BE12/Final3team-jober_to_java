package com.example.final_projects;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FinalProjectsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalProjectsApplication.class, args);
    }

}
