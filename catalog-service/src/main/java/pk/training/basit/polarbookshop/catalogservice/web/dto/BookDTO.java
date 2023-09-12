package pk.training.basit.polarbookshop.catalogservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

// The domain model is implemented as a record, an immutable object
public record BookDTO(

        Long id,

        // Uniquely identifies a book
        @NotBlank(message = "The book ISBN must be defined.")

        // The annotated element must match the specified regular expression (standard ISBN format).
        @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")
        String isbn,

        // The annotated element must not be null and must contain at least one non-whitespace character.
        @NotBlank(message = "The book title must be defined.")
        String title,

        @NotBlank(message = "The book author must be defined.")
        String author,

        @NotNull(message = "The book price must be defined.")

        // The annotated element must not be null and must be greater than zero.
        @Positive(message = "The book price must be greater than zero.")
        Double price,

        String publisher,
        int version
) {

    public static Builder builder(String isbn) {
        return new Builder(isbn);
    }

    //Builder
    public static final class Builder {

        Long id;
        String isbn;
        String title;
        String author;
        Double price;
        String publisher;
        int version;

        public Builder(String isbn) {
            this.isbn = isbn;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder price(Double price) {
            this.price = price;
            return this;
        }

        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public BookDTO build() {
            return new BookDTO(id, isbn, title, author, price, publisher, version);
        }
    }
}
