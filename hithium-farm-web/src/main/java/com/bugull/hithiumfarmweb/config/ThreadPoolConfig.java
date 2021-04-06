package com.bugull.hithiumfarmweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.bugull.hithiumfarmweb.common.Const.WEBTHREAD_POOL_TASK;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        int coreSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        return new ThreadPoolExecutor(coreSize,coreSize,0,TimeUnit.SECONDS, new LinkedBlockingDeque<>(100000),
                new CustomThreadFactory(WEBTHREAD_POOL_TASK), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
