package pk.training.basit.polarbookshop.catalogservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/*
 * The @ConfigurationProperties annotation takes a prefix argument, combined with a field name, to
 * produce the final property key. Spring Boot will try to map all properties with that prefix to
 * fields in the class. In this case, there’s only one property mapped to the bean: polar.greeting.
 * Optionally, you can add a description for each property using JavaDoc comments that can be
 * converted into metadata.
 *
 * Classes or records annotated with @ConfigurationProperties are standard Spring beans, so you can
 * inject them wherever you need them. Spring Boot initializes all the configuration beans at startup
 * and populates them with the data provided through any of the supported configuration data sources.
 * In the case of Catalog Service, the data will be populated from the application.yml file.
 */
// Marks the class as a source for configuration properties starting with the prefix “polar”
@ConfigurationProperties(prefix = "polar")
public class PolarProperties {

	/**
	 * A message to welcome users.
	 */
	// Field for the custom polar.greeting (prefix + field name) property, parsed as String
	private String greeting;

	public String getGreeting() {
		return greeting;
	}

	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}
}
