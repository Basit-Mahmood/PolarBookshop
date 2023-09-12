package pk.training.basit.polarbookshop.catalogservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pk.training.basit.polarbookshop.catalogservice.web.dto.BookDTO;
import pk.training.basit.polarbookshop.catalogservice.exception.BookAlreadyExistsException;
import pk.training.basit.polarbookshop.catalogservice.exception.BookNotFoundException;
import pk.training.basit.polarbookshop.catalogservice.jpa.repository.BookRepository;
import pk.training.basit.polarbookshop.catalogservice.service.impl.BookServiceImpl;

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

    /**
     * The BookServiceImpl class is taking one argument in it's constructor. We create the mock of the one
     * argument required by BookServiceImpl class using the @Mock annotation above.
     *
     * Using the @InjectMocks annotation on BookServiceImpl. Mockitio will inject the above one mock dependency
     * into the BookServiceImpl constructor. And one more thing you can notice is that you don’t even have
     * to create a new BookServiceImpl object. Mockito will inject it for you. We don't have to use the following
     *
     * 		BookServiceImpl bookServiceImpl = new BookServiceImpl(bookRepository);
     *
     * Note @InjectMocks can't be use on interfaces. Like you can not do the following
     *
     * 		@InjectMocks
            private BookService bookService;
     *
     */
    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    @Test
    void whenBookToCreateAlreadyExistsThenThrows() {
        var bookIsbn = "1234561232";
        BookDTO bookDto = createBookDto(bookIsbn, "Title", "Author", 9.90, "Polarsophia");
        when(bookRepository.existsByIsbn(bookIsbn)).thenReturn(true);
        assertThatThrownBy(() -> bookServiceImpl.addBookToCatalog(bookDto))
                .isInstanceOf(BookAlreadyExistsException.class)
                .hasMessage("A book with ISBN " + bookIsbn + " already exists.");
    }

    @Test
    void whenBookToReadDoesNotExistThenThrows() {
        var bookIsbn = "1234561232";
        when(bookRepository.findByIsbn(bookIsbn)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookServiceImpl.viewBookDetails(bookIsbn))
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
