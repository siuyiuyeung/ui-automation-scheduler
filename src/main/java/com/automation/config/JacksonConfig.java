package com.automation.config;

import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .postConfigurer(mapper -> {
                    mapper.getFactory().setStreamWriteConstraints(
                            StreamWriteConstraints.builder()
                                    .maxNestingDepth(2000)
                                    .build()
                    );
                })
                .build();
    }
}
