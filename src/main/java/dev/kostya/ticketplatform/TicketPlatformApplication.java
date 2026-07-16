package dev.kostya.ticketplatform;

import dev.kostya.ticketplatform.auth.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class TicketPlatformApplication {

    static void main(String[] args) {
        SpringApplication.run(TicketPlatformApplication.class, args);
    }

}
