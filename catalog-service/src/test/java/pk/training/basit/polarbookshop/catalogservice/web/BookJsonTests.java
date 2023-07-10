package pk.training.basit.polarbookshop.catalogservice.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import pk.training.basit.polarbookshop.catalogservice.domain.Book;

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
        var book = new Book("1234567890", "Title", "Author", 9.90);
        var jsonContent = json.write(book);
        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
                .isEqualTo(book.isbn());
        assertThat(jsonContent).extractingJsonPathStringValue("@.title")
                .isEqualTo(book.title());
        assertThat(jsonContent).extractingJsonPathStringValue("@.author")
                .isEqualTo(book.author());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.price")
                .isEqualTo(book.price());
    }

    @Test
    void testDeserialize() throws Exception {
        var content = """
                {
                    "isbn": "1234567890",
                    "title": "Title",
                    "author": "Author",
                    "price": 9.90
                }
                """;
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new Book("1234567890", "Title", "Author", 9.90));
    }

}
