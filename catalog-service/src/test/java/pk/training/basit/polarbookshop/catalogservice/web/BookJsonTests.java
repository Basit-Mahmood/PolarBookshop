package pk.training.basit.polarbookshop.catalogservice.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import pk.training.basit.polarbookshop.catalogservice.jpa.entity.Book;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The Book objects returned by the methods in BookController are
 * parsed into JSON objects. By default, Spring Boot automatically configures
 * the Jackson library to parse Java objects into JSON (serialization) and vice
 * versa (deserialization).
 *
 * Using the @JsonTest annotation, you can test JSON serialization and
 * deserialization for your domain objects. @JsonTest loads a Spring
 * application context and auto-configures the JSON mappers for the specific
 * library in use (by default, it’s Jackson). Furthermore, it configures the
 * JacksonTester utility, which you can use to check that the JSON
 * mapping works as expected, relying on the JsonPath and JSONAssert
 * libraries.
 */
@JsonTest
class BookJsonTests {

    @Autowired
    private JacksonTester<Book> json;

    @Test
    void testSerialize() throws Exception {
        var book = Book.builder("1234567890")
                .id(394L)
                .title("Title")
                .author("Author")
                .price(9.90)
                .publisher("Polarsophia")
                .version(21)
                .build();

        var jsonContent = json.write(book);
        assertThat(jsonContent).extractingJsonPathNumberValue("@.id")
                .isEqualTo(book.getId().intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
                .isEqualTo(book.getIsbn());
        assertThat(jsonContent).extractingJsonPathStringValue("@.title")
                .isEqualTo(book.getTitle());
        assertThat(jsonContent).extractingJsonPathStringValue("@.author")
                .isEqualTo(book.getAuthor());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.price")
                .isEqualTo(book.getPrice());
        assertThat(jsonContent).extractingJsonPathStringValue("@.publisher")
                .isEqualTo(book.getPublisher());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.version")
                .isEqualTo(book.getVersion());
    }

    @Test
    void testDeserialize() throws Exception {
        var content = """
                {
                    "id": 394,
                    "isbn": "1234567890",
                    "title": "Title",
                    "author": "Author",
                    "price": 9.90,
                    "publisher": "Polarsophia",
                    "version": 21
                }
                """;

        var book = Book.builder("1234567890")
                .id(394L)
                .title("Title")
                .author("Author")
                .price(9.90)
                .publisher("Polarsophia")
                .version(21)
                .build();

        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(book);
    }

}
