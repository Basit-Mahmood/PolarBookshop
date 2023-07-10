package pk.training.basit.polarbookshop.catalogservice.demo;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pk.training.basit.polarbookshop.catalogservice.domain.Book;
import pk.training.basit.polarbookshop.catalogservice.repository.BookRepository;

@Component

/*
 * Assigns the class to the testdata profile. It will be registered only when the testdata profile
 * is active.
 */
@Profile("testdata")
public class BookDataLoader {

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

		Book book1 = createBook("1234567891", "Northern Lights", "Lyra Silverstar", 9.90);
		Book book2 = createBook("1234567892", "Polar Journey", "Iorek Polarson", 12.90);

		bookRepository.save(book1);
		bookRepository.save(book2);
	}

	private Book createBook(String isbn, String title, String author, Double price) {
		return Book.builder(isbn)
				.title(title)
				.author(author)
				.price(price)
				.build();
	}

}
