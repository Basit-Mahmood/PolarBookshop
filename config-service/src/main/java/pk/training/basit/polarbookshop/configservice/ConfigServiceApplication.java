package pk.training.basit.polarbookshop.configservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication

// Activates the configuration server implementation in the Spring Boot application
@EnableConfigServer
public class ConfigServiceApplication {
	public static void main(String[] args) {
		// http://127.0.0.1:8888/catalog-service/default
		SpringApplication.run(ConfigServiceApplication.class, args);
	}

}
