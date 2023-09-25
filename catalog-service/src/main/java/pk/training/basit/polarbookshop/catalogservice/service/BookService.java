package pk.training.basit.polarbookshop.catalogservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pk.training.basit.polarbookshop.catalogservice.jpa.entity.Book;
import pk.training.basit.polarbookshop.catalogservice.web.dto.BookDTO;

import java.util.Collection;

public interface BookService {

    Page<Book> getAllBooks(Pageable pageable);
    Page<BookDTO> viewBookList(Pageable pageable);
    Book findByIsbn(String isbn);
    BookDTO viewBookDetails(String isbn);
    Book saveBook(Book book);
    Book addNewBook(Book book);
    BookDTO addBookToCatalog(BookDTO bookDto);
    void removeBookFromCatalog(String isbn);
    BookDTO editBookDetails(String isbn, BookDTO bookDto);
}
