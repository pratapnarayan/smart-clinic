package com.smarthospital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableAsync
public class SmartHospitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartHospitalApplication.class, args);
    }
}
