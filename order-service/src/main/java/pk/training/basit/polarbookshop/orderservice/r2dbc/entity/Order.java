package pk.training.basit.polarbookshop.orderservice.r2dbc.entity;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;
import pk.training.basit.polarbookshop.orderservice.enums.OrderStatus;

import java.time.Instant;

/**
 * The default strategy for mapping an entity to a relational table is to transform the Java object name
 * into lowercase. In this example, Spring Data would try to map the Order record to an order table. The
 * problem is that order is a reserved word in SQL. It’s not recommended that you use it as a table name
 * because it would require special handling. You can overcome that issue by naming the table orders and
 * configuring the object-relational mapping through the @Table annotation
 */
@Table(name = "orders")
public record Order(

        @Id
        Long id,
        String bookIsbn,
        String bookName,
        Double bookPrice,
        Integer quantity,
        OrderStatus status,

        @CreatedBy
        Long createdBy,

        @CreatedDate    // When the entity was created
        Instant createdDate,

        @LastModifiedBy
        Long lastModifiedBy,

        @LastModifiedDate   // When the entity was last modified
        Instant lastModifiedDate,

        Instant deletedDate,

        /**
         * When dealing with conflicts, you have two options:
         * <p>
         * - You can try to avoid the conflict, and that's what Pessimistic Locking does.
         * - Or, you could allow the conflict to occur, but you need to detect it upon committing your transactions,
         * and that's what Optimistic Locking does.
         * <p>
         * The Lost Update anomaly can happen in the Read Committed isolation level.
         * <p>
         * Let say in database there is a record with id = 1. Two users can use the following query concurrently to see
         * the record
         * <p>
         * select id, account from amount where id = 1
         * <p>
         * Now let say both users see that they have 50 amount.
         * <p>
         * Now user2 execute the following query
         * <p>
         * update amount set amount = amount - 30 where id = 1     -- amount = 20
         * <p>
         * User1 execute the following query
         * <p>
         * update amount set amount = amount - 40 where id = 1     -- amount = 10
         * <p>
         * User1 believes he can withdraw 40 from her account but does not realize that User2 has just changed the account
         * balance, and now there are only 20 left in this account.
         * <p>
         * Pessimistic Locking:
         * --------------------
         * <p>
         * Pessimistic locking achieves this goal by taking a shared or read lock on the account so User2 is prevented
         * from changing the account.
         * <p>
         * Both User1 and User2 will acquire a read lock on the account table row that both users have read. The
         * database acquires these locks on SQL Server when using Repeatable Read or Serializable.
         * <p>
         * Because both User1 and User2 have read the account with the PK value of 1, neither of them can change it
         * until one user releases the read lock. This is because a write operation requires a write/exclusive lock
         * acquisition, and shared/read locks prevent write/exclusive locks.
         * <p>
         * Only after User1 has committed her transaction and the read lock was released on the account row, User2
         * UPDATE will resume and apply the change. Until User1 releases the read lock, User2 UPDATE blocks.
         * <p>
         * Optimistic Locking:
         * --------------------
         * <p>
         * Optimistic Locking allows the conflict to occur but detects it upon applying User1 UPDATE as the version has
         * changed.
         * <p>
         * update amount set amount = amount - 30, version = 2 where id = 1 and version = 1
         * <p>
         * This time, we have an additional version column. The version column is incremented every time an
         * UPDATE or DELETE is executed, and it is also used in the WHERE clause of the UPDATE and DELETE statements.
         * For this to work, we need to issue the SELECT and read the current version prior to executing the UPDATE
         * or DELETE, as otherwise, we would not know what version value to pass to the WHERE clause or to increment.
         * <p>
         * Application-level transactions:
         * ---------------------------------
         * <p>
         * Relational database systems have emerged in the late 70's early 80's when a client would, typically, connect
         * to a mainframe via a terminal. That's why we still see database systems define terms such as SESSION setting.
         * <p>
         * Nowadays, over the Internet, we no longer execute reads and writes in the context of the same database
         * transaction, and ACID is no longer sufficient.
         * <p>
         * Let say User 1 use the GET request to find his amount
         * - Request will come to web server.
         * - Web server will execute the qeury
         * select * from amount where id = 1
         * <p>
         * - Return the response to the User1.
         * <p>
         * After returning the result there was job executed on the server that uses the following update
         * <p>
         * update amount set amount = amount - 30, version = 2 where id = 1 and version = 1
         * <p>
         * Now User1 use the PUT request to update the amount
         * - Request will come to web server.
         * - Web server will execute the qeury
         * update amount set amount = amount - 40, version = 2 where id = 1 and version = 1
         * <p>
         * - At this point OptimisticLockException will occur
         * <p>
         * Without optimistic locking, there is no way this Lost Update would have been caught even if the database transactions used Serializable. This is because reads and writes are executed in separate HTTP requests, hence on different database transactions.
         * <p>
         * So, optimistic locking can help you prevent Lost Updates even when using application-level transactions
         * that incorporate the user-think time as well.
         * <p>
         * Conclusion:
         * ------------
         * <p>
         * Optimistic locking is a very useful technique, and it works just fine even when using less-strict isolation
         * levels, like Read Committed, or when reads and writes are executed in subsequent database transactions.
         * <p>
         * The downside of optimistic locking is that a rollback will be triggered by the data access framework upon
         * catching an OptimisticLockException, therefore losing all the work we've done previously by the currently
         * executing transaction.
         * <p>
         * The more contention, the more conflicts, and the greater the chance of aborting transactions. Rollbacks
         * can be costly for the database system as it needs to revert all current pending changes which might involve
         * both table rows and index records.
         * <p>
         * For this reason, pessimistic locking might be more suitable when conflicts happen frequently, as it
         * reduces the chance of rolling back transactions.
         * <p>
         * Optimistic locking is used when you don't expect many collisions. It costs less to do a normal operation
         * but if the collision DOES occur you would pay a higher price to resolve it as the transaction is aborted.
         * <p>
         * Pessimistic locking is used when a collision is anticipated. The transactions which would violate
         * synchronization are simply blocked.
         * <p>
         * To select proper locking mechanism you have to estimate the amount of reads and writes and plan accordingly.
         */
        @Version
        int version
) {

    public static Builder builder(String isbn) {
        return new Builder(isbn);
    }

    public static final class Builder {

        Long id;
        String bookIsbn;
        String bookName;
        Double bookPrice;
        Integer quantity;
        OrderStatus status;
        Long createdBy;
        Instant createdDate;
        Long lastModifiedBy;
        Instant lastModifiedDate;
        Instant deletedDate;
        int version;

        public Builder(String bookIsbn) {
            this.bookIsbn = bookIsbn;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder bookName(String bookName) {
            this.bookName = bookName;
            return this;
        }

        public Builder bookPrice(Double bookPrice) {
            this.bookPrice = bookPrice;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdBy(Long createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder createdDate(Instant createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder lastModifiedBy(Long lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
            return this;
        }

        public Builder lastModifiedDate(Instant lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        public Builder deletedDate(Instant deletedDate) {
            this.deletedDate = deletedDate;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public Order build() {
            Order order = new Order(this.id, this.bookIsbn, this.bookName, this.bookPrice, this.quantity, this.status, this.createdBy, this.createdDate, this.lastModifiedBy, this.lastModifiedDate, this.deletedDate, this.version);
            return order;
        }
    }

}
