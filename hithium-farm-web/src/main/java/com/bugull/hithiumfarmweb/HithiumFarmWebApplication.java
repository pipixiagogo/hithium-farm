package com.bugull.hithiumfarmweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HithiumFarmWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(HithiumFarmWebApplication.class, args);
    }



}
