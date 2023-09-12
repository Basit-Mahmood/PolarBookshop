package pk.training.basit.polarbookshop.catalogservice;

import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;

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
