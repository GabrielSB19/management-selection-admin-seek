package com.example.management_selection_admin_seek.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Simple Async Configuration
 * Enables basic asynchronous processing for client operations
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * Simple thread pool for async tasks
     * Basic configuration for client background processing
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(2);          
        executor.setMaxPoolSize(4);            
        executor.setQueueCapacity(50);        
        executor.setThreadNamePrefix("async-");
        
        executor.initialize();
        return executor;
    }
}
