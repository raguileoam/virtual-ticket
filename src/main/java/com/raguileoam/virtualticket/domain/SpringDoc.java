package com.raguileoam.virtualticket.domain;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;

@Configuration
public class SpringDoc {
    @Bean
    @CrossOrigin
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("springshop-public")
                .pathsToMatch("/**")
                .build();
    }
}
