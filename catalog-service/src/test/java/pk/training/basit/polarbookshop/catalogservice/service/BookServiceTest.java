package pk.training.basit.polarbookshop.catalogservice.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pk.training.basit.polarbookshop.catalogservice.domain.Book;
import pk.training.basit.polarbookshop.catalogservice.exception.BookAlreadyExistsException;
import pk.training.basit.polarbookshop.catalogservice.exception.BookNotFoundException;
import pk.training.basit.polarbookshop.catalogservice.repository.BookRepository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests are not aware of Spring and don’t rely on any Spring library. They are intended to test
 * the behavior of single components as isolated units. Any dependency at the edge of the unit is
 * to keep the test shielded from external components.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void whenBookToCreateAlreadyExistsThenThrows() {
        var bookIsbn = "1234561232";
        var bookToCreate = new Book(bookIsbn, "Title", "Author", 9.90);
        when(bookRepository.existsByIsbn(bookIsbn)).thenReturn(true);
        assertThatThrownBy(() -> bookService.addBookToCatalog(bookToCreate))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessage("A book with ISBN " + bookIsbn + " already exists.");
    }

    @Test
    void whenBookToReadDoesNotExistThenThrows() {
        var bookIsbn = "1234561232";
        when(bookRepository.findByIsbn(bookIsbn)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.viewBookDetails(bookIsbn))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("The book with ISBN " + bookIsbn + " was not found.");
    }

}
