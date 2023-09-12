package pk.training.basit.polarbookshop.orderservice.service;

import org.springframework.data.domain.Pageable;
import pk.training.basit.polarbookshop.orderservice.r2dbc.entity.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

    Flux<Order> getAllOrders(Pageable pageable);
    Mono<Order> submitOrder(String isbn, int quantity);

}
