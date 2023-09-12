package pk.training.basit.polarbookshop.catalogservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import pk.training.basit.polarbookshop.catalogservice.web.dto.BookDTO;

import static org.assertj.core.api.Assertions.assertThat;

// Loads a full Spring web application context and a Servlet container listening on a random port
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "300000") // 300 seconds, 5 minutes

// Enables the “integration” profile to load configuration from application-integration.yml
@ActiveProfiles("integration")
class CatalogServiceApplicationTests {

	// Utility to perform REST calls for testing
	@Autowired
	private WebTestClient webTestClient;

	@Test
	void whenGetRequestWithIdThenBookReturned() {
		var bookIsbn = "1231231230";
		var bookToCreate = createBookDto(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
		BookDTO expectedBook = webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(BookDTO.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody(BookDTO.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	@Test
	void whenPostRequestThenBookCreated() {
		var expectedBook = createBookDto("1231231231", "Title", "Author", 9.90, "Polarsophia");

		webTestClient
				.post()
				.uri("/books")
				.bodyValue(expectedBook)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(BookDTO.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
				});
	}

	@Test
	void whenPutRequestThenBookUpdated() {
		var bookIsbn = "1231231232";
		var bookToCreate = createBookDto(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
		BookDTO createdBook = webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(BookDTO.class).value(book -> assertThat(book).isNotNull())
				.returnResult().getResponseBody();

		var bookToUpdate = updateBookDto(createdBook.id(), createdBook.isbn(), createdBook.title(), createdBook.author(), 7.95, createdBook.publisher());

		webTestClient
				.put()
				.uri("/books/" + bookIsbn)
				.bodyValue(bookToUpdate)
				.exchange()
				.expectStatus().isOk()
				.expectBody(BookDTO.class).value(actualBook -> {
					assertThat(actualBook).isNotNull();
					assertThat(actualBook.price()).isEqualTo(bookToUpdate.price());
				});
	}

	@Test
	void whenDeleteRequestThenBookDeleted() {
		var bookIsbn = "1231231233";
		var bookToCreate = createBookDto(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
		webTestClient
				.post()
				.uri("/books")
				.bodyValue(bookToCreate)
				.exchange()
				.expectStatus().isCreated();

		webTestClient
				.delete()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNoContent();

		webTestClient
				.get()
				.uri("/books/" + bookIsbn)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody(String.class).value(errorMessage ->
						assertThat(errorMessage).isEqualTo("The book with ISBN " + bookIsbn + " was not found.")
				);
	}

	private BookDTO createBookDto(String isbn, String title, String author, Double price, String publisher) {
		return getBookDtoBuilder(isbn, title, author, price, publisher)
				.build();
	}

	private BookDTO updateBookDto(Long id, String isbn, String title, String author, Double price, String publisher) {
		return getBookDtoBuilder(isbn, title, author, price, publisher)
				.id(id)
				.build();
	}

	private BookDTO.Builder getBookDtoBuilder(String isbn, String title, String author, Double price, String publisher) {
		return BookDTO.builder(isbn)
				.title(title)
				.author(author)
				.price(price)
				.publisher(publisher);
	}

}
