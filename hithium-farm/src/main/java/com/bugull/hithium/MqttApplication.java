package com.bugull.hithium;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class MqttApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MqttApplication.class)
                .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

}
