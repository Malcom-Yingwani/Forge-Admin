package za.co.heritagebaptist.forge;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@EnableScheduling
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class ForgeApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ForgeApiApplication.class, args);
    }
}
// Temporary setup path-filter acceptance probe.
