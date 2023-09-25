package pk.training.basit.polarbookshop.orderservice.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pk.training.basit.polarbookshop.orderservice.enums.OrderStatus;
import pk.training.basit.polarbookshop.orderservice.r2dbc.entity.Order;
import pk.training.basit.polarbookshop.orderservice.r2dbc.repository.OrderRepository;
import pk.training.basit.polarbookshop.orderservice.service.OrderService;
import pk.training.basit.polarbookshop.orderservice.web.client.BookClient;
import pk.training.basit.polarbookshop.orderservice.web.dto.BookDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final BookClient bookClient;
    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository, BookClient bookClient) {
        this.orderRepository = orderRepository;
        this.bookClient = bookClient;
    }

    // A Flux is used to publish multiple orders (0 ... N)
    @Override
    public Mono<Page<Order>> getAllOrders(Pageable pageable) {
        LOGGER.info("getAllOrders() starts for {}", pageable.getSort());
        Flux<Order> orders = orderRepository.findAllBy(pageable);
        Mono<Page<Order>> pagedOrders = orders.collectList()
                .zipWith(orders.count())
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.size()));

        LOGGER.info("getAllOrders() ends for {}", pageable.getSort());
        return pagedOrders;
    }

    @Override
    public Mono<Order> submitOrder(String isbn, int quantity) {
        LOGGER.info("submitOrder() starts for isbn {} and quantity {}", isbn, quantity);

        /**
         * map vs. flatMap:
         * -----------------
         * When using Reactor, choosing between the map() and flatMap() operators is usually a source of
         * confusion. Both operators return a reactive stream (either Mono<T> or Flux<T>), but while map()
         * maps between two standard Java types, flatMap() maps from a Java type to another reactive stream.
         *
         * Below, we map from an object of type Order to a Mono<Order> (which is returned by OrderRepository).
         * Since the map() operator expects the target type not to be a reactive stream, it will wrap it in
         * one nevertheless and return a Mono<Mono<Order>> object. On the other hand, the flatMap() operator
         * expects the target type to be a reactive stream, so it knows how to handle the publisher produced
         * by OrderRepository and returns a Mono<Order> object correctly.
         */
        Mono<Order> savedOrder = bookClient.getBookByIsbn(isbn)     // Calls the Catalog Service to check the book’s availability
                .map(book -> buildAcceptedOrder(book, quantity))    // If the book is available, it accepts the order.
                .defaultIfEmpty(buildRejectedOrder(isbn, quantity)) // If the book is not available, it rejects the order.
                .flatMap(orderRepository::save);                    // Saves the order (either as accepted or rejected)

        LOGGER.info("submitOrder() ends for isbn {} and quantity {}", isbn, quantity);
        return savedOrder;
    }

    /**
     * When an order is accepted, we specify ISBN, book name (title + author), quantity, and status. Spring
     * Data takes care of adding the identifier, version, and audit metadata.
     */
    public static Order buildAcceptedOrder(BookDTO book, int quantity) {
        return Order.builder(book.isbn())
                .bookName(book.title() + " - " + book.author())
                .bookPrice(book.price())
                .quantity(quantity)
                .status(OrderStatus.ACCEPTED)
                .build();
    }

    public static Order buildRejectedOrder(String bookIsbn, int quantity) {
        /**
         * When an order is rejected, we only specify ISBN, quantity, and status. Spring Data takes care of
         * adding identifier, version, and audit metadata.
         */
        return Order.builder(bookIsbn)
                .quantity(quantity)
                .status(OrderStatus.REJECTED)
                .build();
    }
}
