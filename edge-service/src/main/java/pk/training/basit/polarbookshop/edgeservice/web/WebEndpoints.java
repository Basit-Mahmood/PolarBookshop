package pk.training.basit.polarbookshop.edgeservice.web;

import reactor.core.publisher.Mono;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WebEndpoints {

	/**
	 * Functional endpoints in Spring WebFlux are defined as routes in a RouterFunction<ServerResponse> bean, using
	 * the fluent API provided by RouterFunctions. For each route, you need to define the endpoint URL, a method,
	 * and a handler.
	 *
	 * For simplicity, the fallback for GET requests returns an empty string, whereas the fallback for POST
	 * requests returns an HTTP 503 error. In a real scenario, you might want to adopt different fallback strategies
	 * depending on the context, including throwing a custom exception to be handled from the client or returning
	 * the last value saved in the cache for the original request.
	 */
	@Bean
	public RouterFunction<ServerResponse> routerFunction() {

		// Offers a fluent API to build routes
		return RouterFunctions.route()
				// Fallback response used to handle the GET endpoint
				.GET("/catalog-fallback", request -> ServerResponse.ok().body(Mono.just(""), String.class))
				// Fallback response used to handle the POST endpoint
				.POST("/catalog-fallback", request -> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build())
				// Builds the functional endpoints
				.build();
	}
	
}
