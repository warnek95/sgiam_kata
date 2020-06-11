package com.carbonit.sgiam.kata.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UndertowConfig {

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> containerCustomizer(
            @Value("${server.http.port}") final int httpPort, @Value("${server.http.interface}") final String httpInterface) {
        return factory -> factory.getBuilderCustomizers()
            .add(builder -> builder.addHttpListener(httpPort, httpInterface));
    }
}