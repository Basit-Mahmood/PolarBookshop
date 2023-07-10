package pk.training.basit.polarbookshop.catalogservice.controller.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pk.training.basit.polarbookshop.catalogservice.domain.Book;
import pk.training.basit.polarbookshop.catalogservice.service.BookService;

// Stereotype annotation marking a class as a Spring component and a source of handlers for REST
//endpoints
@RestController

// Identifies the root path mapping URI for which the class provides handlers ("/books")
@RequestMapping("books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Maps HTTP GET requests to the specific handler method
     */
    @GetMapping
    public Iterable<Book> get() {
        return bookService.viewBookList();
    }

    /**
     * A URI template variable appended to the root path mapping URI ("/books/{isbn}")
     */
    @GetMapping("{isbn}")
    public Book getByIsbn(@PathVariable String isbn) { // @PathVariable binds a method parameter to a URI template variable ({isbn}).
        return bookService.viewBookDetails(isbn);
    }

    /**
     * Maps HTTP POST requests to the specific handler method
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Returns a 201 status if the book is created successfully
    public Book post(@Valid @RequestBody Book book) { // @RequestBody binds a method parameter to the body of a web request.
        return bookService.addBookToCatalog(book);
    }

    /**
     * Maps HTTP DELETE requests to the specific handler method
     */
    @DeleteMapping("{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns a 204 status if the book is deleted successfully
    public void delete(@PathVariable String isbn) {
        bookService.removeBookFromCatalog(isbn);
    }

    /**
     * Maps HTTP PUT requests to the specific handler method
     */
    @PutMapping("{isbn}")
    public Book put(@PathVariable String isbn, @Valid @RequestBody Book book) {
        return bookService.editBookDetails(isbn, book);
    }
}
