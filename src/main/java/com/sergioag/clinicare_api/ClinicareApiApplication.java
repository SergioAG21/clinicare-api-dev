package com.sergioag.clinicare_api;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ClinicareApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClinicareApiApplication.class, args);
	}

    @Bean
    public CommandLineRunner printRoutes(ApplicationContext ctx) {
        return args -> {
            System.out.println("🔍 RUTAS DISPONIBLES:");
            var mappings = ctx.getBean("requestMappingHandlerMapping", org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping.class);
            mappings.getHandlerMethods().forEach((key, value) -> {
                System.out.println("➡️  " + key);
            });
        };
    }
}
