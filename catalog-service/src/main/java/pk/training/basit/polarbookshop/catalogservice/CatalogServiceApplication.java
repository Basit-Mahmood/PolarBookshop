package pk.training.basit.polarbookshop.catalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication

/*
 * The first thing to do is tell Spring Boot to scan the application context for configuration
 * data beans. We can do so by adding the @ConfigurationPropertiesScan annotation to the
 * CatalogServiceApplication
 */
@ConfigurationPropertiesScan // Loads configuration data beans in the Spring context
public class CatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogServiceApplication.class, args);
	}
}
