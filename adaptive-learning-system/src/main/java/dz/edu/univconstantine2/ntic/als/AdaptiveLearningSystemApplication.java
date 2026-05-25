package dz.edu.univconstantine2.ntic.als;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Spring Boot application class that bootstraps the Adaptive Learning System application.
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
@EnableAsync
@EnableScheduling
public class AdaptiveLearningSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdaptiveLearningSystemApplication.class, args);
	}

}
