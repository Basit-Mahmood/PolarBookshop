package pk.training.basit.polarbookshop.catalogservice.repository.impl;

import org.springframework.stereotype.Repository;
import pk.training.basit.polarbookshop.catalogservice.domain.Book;
import pk.training.basit.polarbookshop.catalogservice.repository.BookRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Stereotype annotation that marks a class to be a repository managed by Spring
@Repository
public class InMemoryBookRepository implements BookRepository {

    // In-memory map to store books for testing purposes
    private static final Map<String, Book> books = new ConcurrentHashMap<>();

    @Override
    public Iterable<Book> findAll() {
        return books.values();
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return existsByIsbn(isbn) ? Optional.of(books.get(isbn)) : Optional.empty();
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        return books.get(isbn) != null;
    }

    @Override
    public Book save(Book book) {
        books.put(book.isbn(), book);
        return book;
    }

    @Override
    public void deleteByIsbn(String isbn) {
        books.remove(isbn);
    }
}
