package com.rev.g3.i2.ers2;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        // Load .env (DB_USER, DB_PASS, JWT_SECRET, ...) into system properties so that
        // ${DB_USER}-style placeholders in application.properties resolve. Spring does not
        // read .env files on its own. Real environment variables still take precedence.
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(e -> {
            if (System.getenv(e.getKey()) == null && System.getProperty(e.getKey()) == null) {
                System.setProperty(e.getKey(), e.getValue());
            }
        });
        SpringApplication.run(App.class, args);
    }
}
