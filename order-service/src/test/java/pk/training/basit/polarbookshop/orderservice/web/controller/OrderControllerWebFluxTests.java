package pk.training.basit.polarbookshop.orderservice.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import pk.training.basit.polarbookshop.orderservice.enums.OrderStatus;
import pk.training.basit.polarbookshop.orderservice.r2dbc.entity.Order;
import pk.training.basit.polarbookshop.orderservice.service.OrderService;
import pk.training.basit.polarbookshop.orderservice.service.impl.OrderServiceImpl;
import pk.training.basit.polarbookshop.orderservice.web.request.OrderRequest;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

// Identifies a test class that focuses on Spring WebFlux components, targeting OrderController
@WebFluxTest(OrderController.class)
class OrderControllerWebFluxTests {

	// A WebClient variant with extra features to make testing RESTful services easier
	@Autowired
	private WebTestClient webClient;

	// Adds a mock of OrderService to the Spring application context
	@MockBean
	private OrderService orderService;

	@Test
	void whenBookNotAvailableThenRejectOrder() {
		var orderRequest = new OrderRequest("1234567890", 3);
		var expectedOrder = OrderServiceImpl.buildRejectedOrder(orderRequest.isbn(), orderRequest.quantity());

		// Defines the expected behavior for the OrderService mock bean
		given(orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity()))
				.willReturn(Mono.just(expectedOrder));

		webClient
				.post()
				.uri("/orders")
				.bodyValue(orderRequest)
				.exchange()
				.expectStatus().is2xxSuccessful()	// Expects the order is created successfully
				.expectBody(Order.class).value(actualOrder -> {
					assertThat(actualOrder).isNotNull();
					assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
				});
	}

}
