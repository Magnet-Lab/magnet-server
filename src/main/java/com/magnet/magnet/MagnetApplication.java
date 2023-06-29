package com.magnet.magnet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MagnetApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagnetApplication.class, args);
    }

}