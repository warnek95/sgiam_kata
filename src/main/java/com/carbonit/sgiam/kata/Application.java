package com.carbonit.sgiam.kata;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI(@Value("${project.version}") final String appVersion) {
		return new OpenAPI().info(new Info().title("Users API").version(appVersion)
			.description("This is a java project for the SG/IAM interview process."));
	}

	@Bean
	public WebServerFactoryCustomizer<UndertowServletWebServerFactory> containerCustomizer(
			@Value("${server.http.port}") final int httpPort, @Value("${server.http.interface}") final String httpInterface) {
		return factory -> factory.getBuilderCustomizers().add(builder -> builder.addHttpListener(httpPort, httpInterface));
	}
}
