package pk.training.basit.polarbookshop.catalogservice.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pk.training.basit.polarbookshop.catalogservice.dto.BookDTO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests are not aware of Spring and don’t rely on any Spring library. They are intended to test
 * the behavior of single components as isolated units. Any dependency at the edge of the unit is
 * to keep the test shielded from external components.
 *
 * The business logic of an application is usually a sensible area to cover with unit tests. In
 * the Catalog Service application, a good candidate for unit testing might be the validation logic
 * for the Book class. The validation constraints are defined using the Java Validation API
 * annotations, and we are interested in testing that they are applied correctly to the Book class.
 * We can check that in a new BookValidationTests class
 */
class BookValidationTests {

    private static Validator validator;

    // Identifies a block of code executed before all tests in the class
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var book = getBookDto("1234567890", "Title", "Author", 9.90, "Polarsophia");
        Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenIsbnNotDefinedThenValidationFails() {
        var book = getBookDto("", "Title", "Author", 9.90, "Polarsophia");
        Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
        assertThat(violations).hasSize(2);
        List<String> constraintViolationMessages = violations.stream()
                .map(ConstraintViolation::getMessage).collect(Collectors.toList());
        assertThat(constraintViolationMessages)
                .contains("The book ISBN must be defined.")
                .contains("The ISBN format must be valid.");
    }

    @Test
    void whenIsbnDefinedButIncorrectThenValidationFails() {
        var book = getBookDto("a234567890", "Title", "Author", 9.90, "Polarsophia");
        Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The ISBN format must be valid.");
    }

    @Test
    void whenTitleIsNotDefinedThenValidationFails() {
        var book = getBookDto("a234567890", "", "Author", 9.90, "Polarsophia");
        Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book title must be defined.");
    }

    @Test
    void whenAuthorIsNotDefinedThenValidationFails() {
        var book = getBookDto("a234567890", "Title", "", 9.90, "Polarsophia");
        Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book author must be defined.");
    }

    @Test
    void whenPriceIsNotDefinedThenValidationFails() {
        var book = getBookDto("1234567890", "Title", "Author", null, "Polarsophia");
        Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be defined.");
    }

    @Test
    void whenPriceDefinedButZeroThenValidationFails() {
        var book = getBookDto("1234567890", "Title", "Author", 0.0, "Polarsophia");
        Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be greater than zero.");
    }

    @Test
    void whenPriceDefinedButNegativeThenValidationFails() {
        var book = getBookDto("1234567890", "Title", "Author", -9.90, "Polarsophia");
        Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be greater than zero.");
    }

    @Test
    void whenPublisherIsNotDefinedThenValidationSucceeds() {
        var book = getBookDto("1234567890", "Title", "Author", 9.90, null);
        Set<ConstraintViolation<BookDTO>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    private BookDTO getBookDto(String isbn, String title, String author, Double price, String publisher) {
        return BookDTO.builder(isbn)
                .title(title)
                .author(author)
                .price(price)
                .publisher(publisher)
                .build();

    }

}
