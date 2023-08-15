package pk.training.basit.polarbookshop.catalogservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pk.training.basit.polarbookshop.catalogservice.dto.BookDTO;
import pk.training.basit.polarbookshop.catalogservice.exception.BookAlreadyExistsException;
import pk.training.basit.polarbookshop.catalogservice.exception.BookNotFoundException;
import pk.training.basit.polarbookshop.catalogservice.jpa.repository.BookRepository;

import java.util.Optional;

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
        BookDTO bookDto = createBookDto(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
        when(bookRepository.existsByIsbn(bookIsbn)).thenReturn(true);
        assertThatThrownBy(() -> bookService.addBookToCatalog(bookDto))
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

    private BookDTO createBookDto(String isbn, String title, String author, Double price, String publisher) {
        return BookDTO.builder(isbn)
                .title(title)
                .author(author)
                .price(price)
                .publisher(publisher)
                .build();
    }

}
