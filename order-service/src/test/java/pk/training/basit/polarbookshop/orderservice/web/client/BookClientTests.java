package pk.training.basit.polarbookshop.orderservice.web.client;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import pk.training.basit.polarbookshop.orderservice.web.client.BookClient;
import pk.training.basit.polarbookshop.orderservice.web.dto.BookDTO;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * When using mocks, there might be situations where the test results depend on the order in which test cases are
 * executed, which tend to be the same on the same operating system. To prevent unwanted execution dependencies, you
 * can annotate the test class with @TestMethodOrder(MethodOrderer.Random.class) to ensure that a pseudo-random
 * order is used at each execution.
 */
@TestMethodOrder(MethodOrderer.Random.class)
class BookClientTests {

	private MockWebServer mockWebServer;
	private BookClient bookClient;

	@BeforeEach
	void setup() throws IOException {
		this.mockWebServer = new MockWebServer();

		// Starts the mock server before running a test case
		this.mockWebServer.start();

		// Uses the mock server URL as the base URL for WebClient
		var webClient = WebClient.builder()
				.baseUrl(mockWebServer.url("/").uri().toString())
				.build();
		this.bookClient = new BookClient(webClient);
	}

	@AfterEach
	void clean() throws IOException {
		// Shuts the mock server down after completing a test case
		this.mockWebServer.shutdown();
	}

	@Test
	void whenBookExistsThenReturnBook() {
		var bookIsbn = "1234567890";

		// Defines the response to be returned by the mock server
		var mockResponse = new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.setBody("""
							{
								"isbn": %s,
								"title": "Title",
								"author": "Author",
								"price": 9.90,
								"publisher": "Polarsophia"
							}
						""".formatted(bookIsbn));

		// Adds a mock response to the queue processed by the mock server
		mockWebServer.enqueue(mockResponse);

		Mono<BookDTO> book = bookClient.getBookByIsbn(bookIsbn);

		// Initializes a StepVerifier object with the object returned by BookClient
		StepVerifier.create(book)
				.expectNextMatches(b -> b.isbn().equals(bookIsbn))// Asserts that the Book returned has the ISBN requested
				.verifyComplete(); // Verifies that the reactive stream completed successfully
	}

	@Test
	void whenBookNotExistsThenReturnEmpty() {
		var bookIsbn = "1234567891";

		var mockResponse = new MockResponse()
				.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.setResponseCode(404);

		mockWebServer.enqueue(mockResponse);

		StepVerifier.create(bookClient.getBookByIsbn(bookIsbn))
				.expectNextCount(0)
				.verifyComplete();
	}

}