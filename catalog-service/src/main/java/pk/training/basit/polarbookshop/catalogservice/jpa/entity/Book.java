package pk.training.basit.polarbookshop.catalogservice.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pk.training.basit.polarbookshop.catalogservice.jpa.audit.AuditDeletedDate;

import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The book ISBN must be defined.")
    @Pattern(regexp = "^([0-9]{10}|[0-9]{13})$", message = "The ISBN format must be valid.")
    private String isbn;

    @NotBlank(message = "The book title must be defined.")
    private String title;

    @NotBlank(message = "The book author must be defined.")
    private String author;

    @NotNull(message = "The book price must be defined.")
    @Positive(message = "The book price must be greater than zero.")
    private Double price;

    private String publisher;

    @Embedded
    private AuditDeletedDate audit = new AuditDeletedDate();

    /**
     * When dealing with conflicts, you have two options:
     *
     *     - You can try to avoid the conflict, and that's what Pessimistic Locking does.
     *     - Or, you could allow the conflict to occur, but you need to detect it upon committing your transactions,
     *     and that's what Optimistic Locking does.
     *
     * The Lost Update anomaly can happen in the Read Committed isolation level.
     *
     * Let say in database there is a record with id = 1. Two users can use the following query concurrently to see
     * the record
     *
     *      select id, account from amount where id = 1
     *
     * Now let say both users see that they have 50 amount.
     *
     * Now user2 execute the following query
     *
     *      update amount set amount = amount - 30 where id = 1     -- amount = 20
     *
     * User1 execute the following query
     *
     *      update amount set amount = amount - 40 where id = 1     -- amount = 10
     *
     * User1 believes he can withdraw 40 from her account but does not realize that User2 has just changed the account
     * balance, and now there are only 20 left in this account.
     *
     * Pessimistic Locking:
     * --------------------
     *
     * Pessimistic locking achieves this goal by taking a shared or read lock on the account so User2 is prevented
     * from changing the account.
     *
     * Both User1 and User2 will acquire a read lock on the account table row that both users have read. The
     * database acquires these locks on SQL Server when using Repeatable Read or Serializable.
     *
     * Because both User1 and User2 have read the account with the PK value of 1, neither of them can change it
     * until one user releases the read lock. This is because a write operation requires a write/exclusive lock
     * acquisition, and shared/read locks prevent write/exclusive locks.
     *
     * Only after User1 has committed her transaction and the read lock was released on the account row, User2
     * UPDATE will resume and apply the change. Until User1 releases the read lock, User2 UPDATE blocks.
     *
     * Optimistic Locking:
     * --------------------
     *
     * Optimistic Locking allows the conflict to occur but detects it upon applying User1 UPDATE as the version has
     * changed.
     *
     *      update amount set amount = amount - 30, version = 2 where id = 1 and version = 1
     *
     * This time, we have an additional version column. The version column is incremented every time an
     * UPDATE or DELETE is executed, and it is also used in the WHERE clause of the UPDATE and DELETE statements.
     * For this to work, we need to issue the SELECT and read the current version prior to executing the UPDATE
     * or DELETE, as otherwise, we would not know what version value to pass to the WHERE clause or to increment.
     *
     * Application-level transactions:
     * ---------------------------------
     *
     * Relational database systems have emerged in the late 70's early 80's when a client would, typically, connect
     * to a mainframe via a terminal. That's why we still see database systems define terms such as SESSION setting.
     *
     * Nowadays, over the Internet, we no longer execute reads and writes in the context of the same database
     * transaction, and ACID is no longer sufficient.
     *
     * Let say User 1 use the GET request to find his amount
     *      - Request will come to web server.
     *      - Web server will execute the qeury
     *          select * from amount where id = 1
     *
     *      - Return the response to the User1.
     *
     *  After returning the result there was job executed on the server that uses the following update
     *
     *      update amount set amount = amount - 30, version = 2 where id = 1 and version = 1
     *
     * Now User1 use the PUT request to update the amount
     *      - Request will come to web server.
     *      - Web server will execute the qeury
     *          update amount set amount = amount - 40, version = 2 where id = 1 and version = 1
     *
     *      - At this point OptimisticLockException will occur
     *
     * Without optimistic locking, there is no way this Lost Update would have been caught even if the database transactions used Serializable. This is because reads and writes are executed in separate HTTP requests, hence on different database transactions.
     *
     * So, optimistic locking can help you prevent Lost Updates even when using application-level transactions
     * that incorporate the user-think time as well.
     *
     * Conclusion:
     * ------------
     *
     * Optimistic locking is a very useful technique, and it works just fine even when using less-strict isolation
     * levels, like Read Committed, or when reads and writes are executed in subsequent database transactions.
     *
     * The downside of optimistic locking is that a rollback will be triggered by the data access framework upon
     * catching an OptimisticLockException, therefore losing all the work we've done previously by the currently
     * executing transaction.
     *
     * The more contention, the more conflicts, and the greater the chance of aborting transactions. Rollbacks
     * can be costly for the database system as it needs to revert all current pending changes which might involve
     * both table rows and index records.
     *
     * For this reason, pessimistic locking might be more suitable when conflicts happen frequently, as it
     * reduces the chance of rolling back transactions.
     *
     * Optimistic locking is used when you don't expect many collisions. It costs less to do a normal operation
     * but if the collision DOES occur you would pay a higher price to resolve it as the transaction is aborted.
     *
     * Pessimistic locking is used when a collision is anticipated. The transactions which would violate
     * synchronization are simply blocked.
     *
     * To select proper locking mechanism you have to estimate the amount of reads and writes and plan accordingly.
     */
    @Version
    private int version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public AuditDeletedDate getAudit() {
        return audit;
    }

    public void setAudit(AuditDeletedDate audit) {
        this.audit = audit;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Book book = (Book) o;
        return version == book.version      &&
                id.equals(book.id)          &&
                isbn.equals(book.isbn)      &&
                title.equals(book.title)    &&
                author.equals(book.author)  &&
                price.equals(book.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isbn, title, author, price, version);
    }

    public static Builder builder(String isbn) {
        return new Builder(isbn);
    }

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

        public Book build() {
            Book book = new Book();
            book.id = this.id;
            book.isbn = this.isbn;
            book.title = this.title;
            book.author = this.author;
            book.price = this.price;
            book.publisher = this.publisher;
            book.version = this.version;

            return book;
        }
    }

}
