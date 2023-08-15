package pk.training.basit.polarbookshop.catalogservice.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import pk.training.basit.polarbookshop.catalogservice.jpa.entity.Book;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long> {

    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);

    /**
     * DEFINING TRANSACTIONAL CONTEXTS:
     * --------------------------------
     *
     * The repositories provided by Spring Data come configured with transactional contexts for all the
     * operations. For example, all methods in CrudRepository are transactional. That means you can safely call the
     * saveAll() method, knowing that it will be executed in a transaction.
     *
     * When you add your own query methods, as you did for BookRepository, it’s up to you to define which ones
     * should be part of a transaction. You can rely on the declarative transaction management provided by the
     * Spring Framework and use the @Transactional annotation (from the org.springframework.transaction.annotation
     * package) on classes or methods to ensure they are executed as part of a single unit of work.
     *
     * Among the custom methods you defined in BookRepository, deleteByIsbn() is a good candidate for being
     * transactional, since it modifies the database state. You can ensure it runs in a transaction by applying the
     * @Transactional annotation.
     */
    @Transactional
    void deleteByIsbn(String isbn);
}
