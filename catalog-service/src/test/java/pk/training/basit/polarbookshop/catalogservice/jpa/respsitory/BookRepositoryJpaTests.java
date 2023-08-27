package pk.training.basit.polarbookshop.catalogservice.jpa.respsitory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import pk.training.basit.polarbookshop.catalogservice.config.JpaAuditingConfiguration;
import pk.training.basit.polarbookshop.catalogservice.jpa.entity.Book;
import pk.training.basit.polarbookshop.catalogservice.jpa.repository.BookRepository;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Identifies a test class that focuses on Spring Data JPA components. The @DataJdbcTest annotation encapsulates
 * handy features. For example, it makes each test method run in a transaction and rolls it back at its end, keeping
 * the database clean. After running the test method, the database will not contain the book created in
 * findBookByIsbnWhenExisting() because the transaction is rolled back at the end of the method’s execution.
 */
@DataJpaTest
@Import(JpaAuditingConfiguration.class)     // Imports the data configuration (needed to enable auditing)

// Disables the default behavior of relying on an embedded test database since we want to use Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

// Enables the “integration” profile to load configuration from application-integration.yml
@ActiveProfiles("integration")
public class BookRepositoryJpaTests {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllBooks() {
        var book1 = createBook("1234561235", "Title", "Author", 12.90, "Polarsophia");
        var book2 = createBook("1234561236", "Another Title", "Author", 12.90, "Polarsophia");
        entityManager.persist(book1);
        entityManager.persist(book2);

        Iterable<Book> actualBooks = bookRepository.findAll();

        assertThat(StreamSupport.stream(actualBooks.spliterator(), true)
                           .filter(book -> book.getIsbn().equals(book1.getIsbn()) || book.getIsbn().equals(book2.getIsbn()))
                           .collect(Collectors.toList())).hasSize(2);
    }

    @Test
    void findBookByIsbnWhenExisting() {
        var bookIsbn = "1234561237";
        var book = createBook(bookIsbn, "Title", "Author", 12.90, "Polarsophia");
        entityManager.persist(book);

        Optional<Book> actualBook = bookRepository.findByIsbn(bookIsbn);

        assertThat(actualBook).isPresent();
        assertThat(actualBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    void findBookByIsbnWhenNotExisting() {
        Optional<Book> actualBook = bookRepository.findByIsbn("1234561238");
        assertThat(actualBook).isEmpty();
    }

    @Test
    void existsByIsbnWhenExisting() {
        var bookIsbn = "1234561239";
        var bookToCreate = createBook(bookIsbn, "Title", "Author", 12.90, "Polarsophia");
        entityManager.persist(bookToCreate);

        boolean existing = bookRepository.existsByIsbn(bookIsbn);

        assertThat(existing).isTrue();
    }

    @Test
    void existsByIsbnWhenNotExisting() {
        boolean existing = bookRepository.existsByIsbn("1234561240");
        assertThat(existing).isFalse();
    }

    @Test
    void deleteByIsbn() {
        var bookIsbn = "1234561241";
        var bookToCreate = createBook(bookIsbn, "Title", "Author", 12.90, "Polarsophia");
        var persistedBook = entityManager.persist(bookToCreate);

        bookRepository.deleteByIsbn(bookIsbn);

        assertThat(entityManager.find(Book.class, persistedBook.getId())).isNull();
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
