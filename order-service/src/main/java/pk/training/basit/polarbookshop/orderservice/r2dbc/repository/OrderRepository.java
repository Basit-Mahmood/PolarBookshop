package pk.training.basit.polarbookshop.orderservice.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import pk.training.basit.polarbookshop.orderservice.r2dbc.entity.Order;

public interface OrderRepository extends ReactiveCrudRepository<Order,Long> {
}
