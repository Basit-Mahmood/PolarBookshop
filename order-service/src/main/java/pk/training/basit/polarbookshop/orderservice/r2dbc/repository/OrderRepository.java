package pk.training.basit.polarbookshop.orderservice.r2dbc.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import pk.training.basit.polarbookshop.orderservice.r2dbc.entity.Order;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveSortingRepository<Order, Long>, ReactiveCrudRepository<Order,Long> {

    Flux<Order> findAllBy(Pageable pageable);
}
