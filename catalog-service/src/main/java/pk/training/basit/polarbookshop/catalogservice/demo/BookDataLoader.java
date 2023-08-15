package pk.training.basit.polarbookshop.catalogservice.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pk.training.basit.polarbookshop.catalogservice.dto.BookDTO;
import pk.training.basit.polarbookshop.catalogservice.jpa.entity.Book;
import pk.training.basit.polarbookshop.catalogservice.jpa.repository.BookRepository;

import java.util.Collection;
import java.util.List;


@Component

/*
 * Assigns the class to the testdata profile. It will be registered only when the testdata profile
 * is active.
 */
@Profile("testdata")
public class BookDataLoader {

	private static final Logger LOGGER = LogManager.getLogger();

	private final BookRepository bookRepository;

	public BookDataLoader(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	/**
	 * The test data generation is triggered when an ApplicationReadyEvent is sent—that is when the
	 * application startup phase is completed.
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void loadBookTestData() {

		LOGGER.info("loadBookTestData() starts. Emptying Database");
		// Deletes all existing books, if any, to start from an empty database
		bookRepository.deleteAll();
		LOGGER.info("Database is empty now");

		Book book1 = createBook("1234567891", "Northern Lights", "Lyra Silverstar", 9.90, "Polarsophia");
		Book book2 = createBook("1234567892", "Polar Journey", "Iorek Polarson", 12.90, "Polarsophia1");

		// Saves multiple objects at once
		bookRepository.saveAll(List.of(book1, book2));
		LOGGER.info("book1 {} and book2 {} are successfully saved in database.", book1.getIsbn(), book2.getIsbn());
		LOGGER.info("loadBookTestData() ends");
	}

	private Book createBook(String isbn, String title, String author, Double price, String publisher) {
		return Book.builder(isbn)
				.title(title)
				.author(author)
				.price(price)
				.publisher(publisher)
				.build();
	}

}
