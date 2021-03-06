package dice.server.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableAsync
@EnableScheduling
@EnableJpaRepositories(basePackages = { "dice" })
@EntityScan(basePackages = { "dice" })
@EnableWebSecurity
public class AppConfig {

}
