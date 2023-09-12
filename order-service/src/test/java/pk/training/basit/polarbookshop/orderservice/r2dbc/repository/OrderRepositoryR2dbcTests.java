package pk.training.basit.polarbookshop.orderservice.r2dbc.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pk.training.basit.polarbookshop.orderservice.config.ReactiveAuditingConfiguration;
import pk.training.basit.polarbookshop.orderservice.enums.OrderStatus;
import pk.training.basit.polarbookshop.orderservice.service.OrderService;
import pk.training.basit.polarbookshop.orderservice.service.impl.OrderServiceImpl;
import reactor.test.StepVerifier;

@DataR2dbcTest                                  // Identifies a test class that focuses on R2DBC components
@Import(ReactiveAuditingConfiguration.class)    // Imports R2DBC configuration needed to enable auditing
@Testcontainers                                 // Activates automatic startup and cleanup of test containers
class OrderRepositoryR2dbcTests {

    // Identifies a PostgreSQL container for testing
    @Container
    static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"));

    @Autowired
    private OrderRepository orderRepository;

    // Overwrites R2DBC and Flyway configuration to point to the test PostgreSQL instance
    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", OrderRepositoryR2dbcTests::r2dbcUrl);
        registry.add("spring.r2dbc.username", postgresql::getUsername);
        registry.add("spring.r2dbc.password", postgresql::getPassword);
        registry.add("spring.flyway.url", postgresql::getJdbcUrl);
    }

    // Builds an R2DBC connection string, because Testcontainers doesn’t provide one out of the box as it
    // does for JDBC
    private static String r2dbcUrl() {
        return String.format("r2dbc:postgresql://%s:%s/%s", postgresql.getHost(),
                postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), postgresql.getDatabaseName());
    }

    @Test
    void findOrderByIdWhenNotExisting() {
        StepVerifier.create(orderRepository.findById(394L))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void createRejectedOrder() {
        var rejectedOrder = OrderServiceImpl.buildRejectedOrder("1234567890", 3);

        // Initializes a StepVerifier object with the object returned by OrderRepository
        StepVerifier.create(orderRepository.save(rejectedOrder))
                .expectNextMatches(order -> order.status().equals(OrderStatus.REJECTED))    // Asserts that the Order returned has the correct status
                .verifyComplete();      // Verifies that the reactive stream completed successfully
    }

}
