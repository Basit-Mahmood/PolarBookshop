package pk.training.basit.polarbookshop.edgeservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

// Loads a full Spring web application context and a web environment listening on a random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// Activates automatic startup and cleanup of test containers
@Testcontainers
class EdgeServiceApplicationTests {

	private static final int REDIS_PORT = 6379;

	// Defines a Redis container for testing
	@Container
	static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2"))
			.withExposedPorts(REDIS_PORT);

	// Overwrites the Redis configuration to point to the test Redis instance
	@DynamicPropertySource
	static void redisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.redis.host", () -> redis.getHost());
		registry.add("spring.redis.port", () -> redis.getMappedPort(REDIS_PORT));
	}

	// An empty test used to verify that the application context is loaded correctly and that a connection
	//with Redis has been established successfully
	@Test
	void verifyThatSpringContextLoads() {
	}

}
