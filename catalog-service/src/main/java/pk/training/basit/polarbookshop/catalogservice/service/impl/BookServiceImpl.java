package pk.training.basit.polarbookshop.catalogservice.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pk.training.basit.polarbookshop.catalogservice.web.dto.BookDTO;
import pk.training.basit.polarbookshop.catalogservice.exception.BookAlreadyExistsException;
import pk.training.basit.polarbookshop.catalogservice.exception.BookNotFoundException;
import pk.training.basit.polarbookshop.catalogservice.jpa.entity.Book;
import pk.training.basit.polarbookshop.catalogservice.jpa.repository.BookRepository;
import pk.training.basit.polarbookshop.catalogservice.mapper.BookMapper;
import pk.training.basit.polarbookshop.catalogservice.service.BookService;

// Stereotype annotation that marks a class to be a service managed by Spring
@Service
public class BookServiceImpl implements BookService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final BookRepository bookRepository;

    // BookRepository is provided through constructor auto-wiring.
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Page<BookDTO> viewBookList(Pageable pageable) {
        LOGGER.info("viewBookList() starts for {}", pageable.getSort());
        Page<Book> books = getAllBooks(pageable);
        Page<BookDTO> pagedBooksDto = books
                .map(book -> BookMapper.bookToBookDtoMapper.apply(book));
        LOGGER.info("viewBookList() ends for {}", pageable.getSort());
        return pagedBooksDto;
    }

    @Override
    public Book findByIsbn(String isbn) {
        // When trying to view a book that doesn’t exist, a dedicated exception is thrown.
        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    @Override
    public BookDTO viewBookDetails(String isbn) {
        LOGGER.info("viewBookDetails() starts for book {}", isbn);
        Book book = findByIsbn(isbn);
        BookDTO bookDto = BookMapper.bookToBookDtoMapper.apply(book);
        LOGGER.info("viewBookDetails() ends for book {}", isbn);
        return bookDto;
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book addNewBook(Book book) {
        String isbn = book.getIsbn();
        // When adding the same book to the catalog multiple times, a dedicated exception is thrown.
        if (bookRepository.existsByIsbn(isbn)) {
            throw new BookAlreadyExistsException(isbn);
        }

        return saveBook(book);
    }

    @Override
    public BookDTO addBookToCatalog(BookDTO bookDto) {
        LOGGER.info("addBookToCatalog() starts for book {}", bookDto.isbn());

        Book book = BookMapper.bookDtoToBookMapper.apply(bookDto);
        Book newBook = addNewBook(book);
        BookDTO newBookDto = BookMapper.bookToBookDtoMapper.apply(newBook);
        LOGGER.info("addBookToCatalog() ends for book {}", bookDto.isbn());
        return newBookDto;
    }

    @Override
    public void removeBookFromCatalog(String isbn) {
        LOGGER.info("removeBookFromCatalog() starts for book {}", isbn);
        bookRepository.deleteByIsbn(isbn);
        LOGGER.info("removeBookFromCatalog() ends for book {}", isbn);
    }

    @Override
    public BookDTO editBookDetails(String isbn, BookDTO bookDto) {
        LOGGER.info("editBookDetails() starts for book {}", isbn);
        /**
         * When editing the book, all the Book fields can be updated except the ISBN code,
         * because it’s the entity identifier.
         */
        return bookRepository.findByIsbn(isbn)
				.map(existingBook -> {
                    Book modifiedBook = BookMapper.updateBookMapper.apply(existingBook, bookDto);
                    Book updatedBook = saveBook(modifiedBook);
                    LOGGER.info("Book {} updated successfully", isbn);
                    LOGGER.info("editBookDetails() ends for book {}", isbn);
                    return BookMapper.bookToBookDtoMapper.apply(updatedBook);
				})
                // When changing the details for a book not in the catalog yet, create a new book.
				.orElseGet(() -> {
                    BookDTO newBook = addBookToCatalog(bookDto);
                    LOGGER.info("Book {} added successfully", isbn);
                    LOGGER.info("editBookDetails() ends for book {}", isbn);
                    return newBook;
                });
    }
}
