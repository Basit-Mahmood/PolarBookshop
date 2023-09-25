package pk.training.basit.polarbookshop.catalogservice.mapper;

import pk.training.basit.polarbookshop.catalogservice.web.dto.BookDTO;
import pk.training.basit.polarbookshop.catalogservice.jpa.entity.Book;

import java.util.function.BiFunction;
import java.util.function.Function;

public class BookMapper {

    public static Function<BookDTO, Book> bookDtoToBookMapper = (bookDto) -> {
        return Book.builder(bookDto.isbn())
                .title(bookDto.title())
                .author(bookDto.author())
                .price(bookDto.price())
                .publisher(bookDto.publisher())
                .build();
    };

    public static Function<Book, BookDTO> bookToBookDtoMapper = (book) -> {
        return BookDTO.builder(book.getIsbn())
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .publisher(book.getPublisher())
                .version(book.getVersion())
                .build();
    };

    public static BiFunction<Book, BookDTO, Book> updateBookMapper = (existingBook, bookDto) -> {
        existingBook.setTitle(bookDto.title());
        existingBook.setAuthor(bookDto.author());
        existingBook.setPrice(bookDto.price());
        existingBook.setPublisher(bookDto.publisher());
        return existingBook;
    };

}
