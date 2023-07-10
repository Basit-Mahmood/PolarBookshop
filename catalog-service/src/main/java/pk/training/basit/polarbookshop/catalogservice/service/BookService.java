package pk.training.basit.polarbookshop.catalogservice.service;

import pk.training.basit.polarbookshop.catalogservice.domain.Book;

public interface BookService {

    Iterable<Book> viewBookList();
    Book viewBookDetails(String isbn);
    Book addBookToCatalog(Book book);
    void removeBookFromCatalog(String isbn);
    Book editBookDetails(String isbn, Book book);
}
