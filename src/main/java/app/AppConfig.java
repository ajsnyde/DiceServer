package app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableAsync
@EnableScheduling
@EnableJpaRepositories(basePackages = { "app", "storage", "dice", "store" })
@EntityScan(basePackages = { "app", "storage", "dice", "store" })
// @EnableWebSecurity
public class AppConfig {

}