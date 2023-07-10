package pk.training.basit.polarbookshop.catalogservice.service.impl;

import org.springframework.stereotype.Service;
import pk.training.basit.polarbookshop.catalogservice.domain.Book;
import pk.training.basit.polarbookshop.catalogservice.exception.BookAlreadyExistsException;
import pk.training.basit.polarbookshop.catalogservice.exception.BookNotFoundException;
import pk.training.basit.polarbookshop.catalogservice.repository.BookRepository;
import pk.training.basit.polarbookshop.catalogservice.service.BookService;

// Stereotype annotation that marks a class to be a service managed by Spring
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    // BookRepository is provided through constructor auto-wiring.
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Iterable<Book> viewBookList() {
        return bookRepository.findAll();
    }

    @Override
    public Book viewBookDetails(String isbn) {
        // When trying to view a book that doesn’t exist, a dedicated exception is thrown.
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    @Override
    public Book addBookToCatalog(Book book) {
        // When adding the same book to the catalog multiple times, a dedicated exception is thrown.
        if (bookRepository.existsByIsbn(book.isbn())) {
            throw new BookAlreadyExistsException(book.isbn());
        }
        return bookRepository.save(book);
    }

    @Override
    public void removeBookFromCatalog(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }

    @Override
    public Book editBookDetails(String isbn, Book book) {
        /**
         * When editing the book, all the Book fields can be updated except the ISBN code,
         * because it’s the entity identifier.
         */
        return bookRepository.findByIsbn(isbn)
				.map(existingBook -> {
					var bookToUpdate = new Book(
							existingBook.isbn(),
							book.title(),
							book.author(),
							book.price());
					return bookRepository.save(bookToUpdate);
				})
                // When changing the details for a book not in the catalog yet, create a new book.
				.orElseGet(() -> addBookToCatalog(book));
    }
}
