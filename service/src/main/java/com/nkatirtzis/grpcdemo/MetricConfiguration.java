package com.nkatirtzis.grpcdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@Configuration
public class MetricConfiguration {
    @Bean
    public SimpleMeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
}
