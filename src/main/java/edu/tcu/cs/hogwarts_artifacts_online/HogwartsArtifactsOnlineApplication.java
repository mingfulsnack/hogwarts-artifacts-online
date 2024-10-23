package edu.tcu.cs.hogwarts_artifacts_online;

import edu.tcu.cs.hogwarts_artifacts_online.artifact.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "edu.tcu.cs.hogwarts_artifacts_online")
public class HogwartsArtifactsOnlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(HogwartsArtifactsOnlineApplication.class, args);
	}
	@Bean
	public IdWorker idWorker() {
		return new IdWorker(1,1);
	}
}
