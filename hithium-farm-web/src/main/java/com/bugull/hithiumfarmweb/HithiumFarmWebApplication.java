package com.bugull.hithiumfarmweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = MongoAutoConfiguration.class)
@EnableScheduling
public class HithiumFarmWebApplication{

    public static void main(String[] args) {
        SpringApplication.run(HithiumFarmWebApplication.class, args);
    }


//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(HithiumFarmWebApplication.class);
//    }

}
