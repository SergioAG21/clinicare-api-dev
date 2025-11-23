package com.sergioag.clinicare_api;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ClinicareApiApplication {

	public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        );
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
