package pk.training.basit.polarbookshop.orderservice.web.client;

import java.time.Duration;

import pk.training.basit.polarbookshop.orderservice.web.dto.BookDTO;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class BookClient {

	private static final String BOOKS_ROOT_API = "/books/";
	private final WebClient webClient;

	public BookClient(WebClient webClient) {
		this.webClient = webClient;	// A WebClient bean as configured previously
	}

	public Mono<BookDTO> getBookByIsbn(String isbn) {
		return webClient
				.get()								// The request should use the GET method.
				.uri(BOOKS_ROOT_API + isbn)		// The target URI of the request is /books/{isbn}.
				.retrieve()							// Sends the request and retrieves the response
				.bodyToMono(BookDTO.class)			// Returns the retrieved object as Mono<Book>
				.timeout(Duration.ofSeconds(3), Mono.empty())	// Sets a 3-second timeout for the GET request. The fallback returns an empty Mono object.
				.onErrorResume(WebClientResponseException.NotFound.class, exception -> Mono.empty())	// Returns an empty object when a 404 response is received
				.retryWhen(Retry.backoff(3, Duration.ofMillis(100)))  // Exponential backoff is used as the retry strategy. Three attempts are allowed with a 100 ms initial backoff.
				.onErrorResume(Exception.class, exception -> Mono.empty());	// If any error happens after the 3 retry attempts, catch the exception and return an empty object.

	}

}
