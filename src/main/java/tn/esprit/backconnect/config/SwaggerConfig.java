package tn.esprit.backconnect.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI backConnectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BackConnect - Event Management API")
                        .description("REST API for Event Management with Gamification System (Duolingo-style)")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BackConnect Team")
                                .email("contact@backconnect.tn")));
    }
}
