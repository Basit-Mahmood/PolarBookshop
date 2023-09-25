package pk.training.basit.polarbookshop.catalogservice.web.controller.rest;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pk.training.basit.polarbookshop.catalogservice.service.BookService;
import pk.training.basit.polarbookshop.catalogservice.web.dto.BookDTO;
import pk.training.basit.polarbookshop.catalogservice.web.dto.PagedResponse;

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
    public PagedResponse get(@SortDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BookDTO> pagedBooksDto = bookService.viewBookList(pageable);
        return PagedResponse.builder(pagedBooksDto).build();
    }

    /**
     * A URI template variable appended to the root path mapping URI ("/books/{isbn}")
     */
    @GetMapping("{isbn}")
    public BookDTO getByIsbn(@PathVariable String isbn) { // @PathVariable binds a method parameter to a URI template variable ({isbn}).
        return bookService.viewBookDetails(isbn);
    }

    /**
     * Maps HTTP POST requests to the specific handler method
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Returns a 201 status if the book is created successfully
    public BookDTO post(@Valid @RequestBody BookDTO bookDto) { // @RequestBody binds a method parameter to the body of a web request.
        return bookService.addBookToCatalog(bookDto);
    }

    /**
     * Maps HTTP PUT requests to the specific handler method
     */
    @PutMapping("{isbn}")
    public BookDTO put(@PathVariable String isbn, @Valid @RequestBody BookDTO bookDto) {
        return bookService.editBookDetails(isbn, bookDto);
    }

    /**
     * Maps HTTP DELETE requests to the specific handler method
     */
    @DeleteMapping("{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Returns a 204 status if the book is deleted successfully
    public void delete(@PathVariable String isbn) {
        bookService.removeBookFromCatalog(isbn);
    }

}
