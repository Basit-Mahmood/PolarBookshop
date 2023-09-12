package pk.training.basit.polarbookshop.catalogservice.service;

import org.springframework.data.domain.Pageable;
import pk.training.basit.polarbookshop.catalogservice.web.dto.BookDTO;

import java.util.Collection;

public interface BookService {

    Collection<BookDTO> viewBookList(Pageable pageable);
    BookDTO viewBookDetails(String isbn);
    BookDTO addBookToCatalog(BookDTO bookDto);
    void removeBookFromCatalog(String isbn);
    BookDTO editBookDetails(String isbn, BookDTO bookDto);
}
