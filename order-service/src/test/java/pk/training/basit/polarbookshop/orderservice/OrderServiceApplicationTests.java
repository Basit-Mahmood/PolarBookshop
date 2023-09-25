package pk.training.basit.polarbookshop.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import pk.training.basit.polarbookshop.orderservice.enums.OrderStatus;
import pk.training.basit.polarbookshop.orderservice.r2dbc.entity.Order;
import pk.training.basit.polarbookshop.orderservice.web.client.BookClient;
import pk.training.basit.polarbookshop.orderservice.web.dto.BookDTO;
import pk.training.basit.polarbookshop.orderservice.web.dto.PagedResponse;
import pk.training.basit.polarbookshop.orderservice.web.request.OrderRequest;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class OrderServiceApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.3"));

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private BookClient bookClient;

	@DynamicPropertySource
	static void postgresqlProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.r2dbc.url", OrderServiceApplicationTests::r2dbcUrl);
		registry.add("spring.r2dbc.username", postgresql::getUsername);
		registry.add("spring.r2dbc.password", postgresql::getPassword);
		registry.add("spring.flyway.url", postgresql::getJdbcUrl);
	}

	private static String r2dbcUrl() {
		return String.format("r2dbc:postgresql://%s:%s/%s", postgresql.getHost(),
				postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), postgresql.getDatabaseName());
	}

	@Test
	void whenGetOrdersThenReturn() {
		String bookIsbn = "1234567893";
		BookDTO book = new BookDTO(bookIsbn, "Title", "Author", 9.90);
		given(bookClient.getBookByIsbn(bookIsbn)).willReturn(Mono.just(book));
		OrderRequest orderRequest = new OrderRequest(bookIsbn, 1);
		Order expectedOrder = webTestClient.post().uri("/orders")
				.bodyValue(orderRequest)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Order.class).returnResult().getResponseBody();

		assertThat(expectedOrder).isNotNull();

		webTestClient.get()
				.uri(uriBuilder -> uriBuilder
						.path("/orders")
						.queryParam("page", 0)
						.queryParam("size", 20)
						.queryParam("sort", "id", "desc")
						.build()
				)
				//.uri("/orders")
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(PagedResponse.class).value(pagedResponse -> {
					var orders = (List<LinkedHashMap<String, Object>>) pagedResponse.content();
					assertThat(
							orders.stream()
									.filter(order -> order.get("bookIsbn").equals(bookIsbn))
									.findAny()
					).isNotEmpty();
				});
	}

	@Test
	void whenPostRequestAndBookExistsThenOrderAccepted() {
		String bookIsbn = "1234567899";
		BookDTO book = new BookDTO(bookIsbn, "Title", "Author", 9.90);
		given(bookClient.getBookByIsbn(bookIsbn)).willReturn(Mono.just(book));
		OrderRequest orderRequest = new OrderRequest(bookIsbn, 3);

		Order createdOrder = webTestClient.post().uri("/orders")
				.bodyValue(orderRequest)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Order.class).returnResult().getResponseBody();

		assertThat(createdOrder).isNotNull();
		assertThat(createdOrder.bookIsbn()).isEqualTo(orderRequest.isbn());
		assertThat(createdOrder.quantity()).isEqualTo(orderRequest.quantity());
		assertThat(createdOrder.bookName()).isEqualTo(book.title() + " - " + book.author());
		assertThat(createdOrder.bookPrice()).isEqualTo(book.price());
		assertThat(createdOrder.status()).isEqualTo(OrderStatus.ACCEPTED);
	}

	@Test
	void whenPostRequestAndBookNotExistsThenOrderRejected() {
		String bookIsbn = "1234567894";
		given(bookClient.getBookByIsbn(bookIsbn)).willReturn(Mono.empty());
		OrderRequest orderRequest = new OrderRequest(bookIsbn, 3);

		Order createdOrder = webTestClient.post().uri("/orders")
				.bodyValue(orderRequest)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(Order.class).returnResult().getResponseBody();

		assertThat(createdOrder).isNotNull();
		assertThat(createdOrder.bookIsbn()).isEqualTo(orderRequest.isbn());
		assertThat(createdOrder.quantity()).isEqualTo(orderRequest.quantity());
		assertThat(createdOrder.status()).isEqualTo(OrderStatus.REJECTED);
	}

}
