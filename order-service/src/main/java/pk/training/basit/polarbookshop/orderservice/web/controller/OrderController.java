package pk.training.basit.polarbookshop.orderservice.web.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import pk.training.basit.polarbookshop.orderservice.web.request.OrderRequest;
import pk.training.basit.polarbookshop.orderservice.r2dbc.entity.Order;
import pk.training.basit.polarbookshop.orderservice.service.OrderService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // A Flux is used to publish multiple orders (0 .. N).
    @GetMapping
    public Flux<Order> getAllOrders(Pageable pageable) {
        return orderService.getAllOrders(pageable);
    }

    /**
     * Accepts an OrderRequest object, validated and used to create an order. The created order is
     * returned as a Mono.
     */
    @PostMapping
    public Mono<Order> submitOrder(@RequestBody @Valid OrderRequest orderRequest) {
        return orderService.submitOrder(orderRequest.isbn(), orderRequest.quantity());
    }
}
