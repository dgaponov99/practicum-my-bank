package com.github.dgaponov99.practicum.mybank.accounts.config;

import io.micrometer.context.ContextSnapshotFactory;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "asyncTaskExecutor")
    public ThreadPoolTaskExecutor asyncTaskExecutor(ThreadPoolTaskExecutorBuilder threadPoolTaskExecutorBuilder) {
        return threadPoolTaskExecutorBuilder
                .taskDecorator(contextDecorator())
                .build();
    }

    @Bean
    public TaskDecorator contextDecorator() {
        return runnable -> {
            var snapshot = contextSnapshotFactory().captureAll();
            return () -> snapshot.wrap(runnable).run();
        };
    }

    @Bean
    public ContextSnapshotFactory contextSnapshotFactory() {
        return ContextSnapshotFactory.builder().build();
    }

}
