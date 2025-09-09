package com.example.final_projects.support;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class MailService {
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    public void send(String to, String subject, String body){
        //지금은 콘솔로만
        log.info("메일 전송 (stub)\nTo: {}\nSubject: {}\nBody: {}", to, subject, body);
    }

    @Autowired
    DataSource dataSource;

    @PostConstruct
    public void printJdbcUrl() throws Exception {
        try (var conn = dataSource.getConnection()) {
            var md = conn.getMetaData();
            System.out.println(">>> JDBC URL = " + md.getURL());
            System.out.println(">>> JDBC User = " + md.getUserName());
            try (var rs = conn.createStatement().executeQuery("SELECT DATABASE(), @@hostname, @@port")) {
                if (rs.next()) {
                    System.out.println(">>> DB()=" + rs.getString(1)
                            + " host=" + rs.getString(2) + " port=" + rs.getString(3));
                }
            }
        }
    }
}
