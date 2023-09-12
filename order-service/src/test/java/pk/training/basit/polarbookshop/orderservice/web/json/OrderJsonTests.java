package pk.training.basit.polarbookshop.orderservice.web.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import pk.training.basit.polarbookshop.orderservice.enums.OrderStatus;
import pk.training.basit.polarbookshop.orderservice.r2dbc.entity.Order;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class OrderJsonTests {

    @Autowired
    private JacksonTester<Order> json;

    @Test
    void testSerialize() throws Exception {

        var order = Order.builder("1234567890")
                .id(394L)
                .bookName("Book Name")
                .bookPrice(9.90)
                .quantity(1)
                .status(OrderStatus.ACCEPTED)
                .createdBy(0L)
                .createdDate(Instant.now())
                .lastModifiedBy(0L)
                .lastModifiedDate(Instant.now())
                .version(21)
                .build();

        var jsonContent = json.write(order);

        assertThat(jsonContent).extractingJsonPathNumberValue("@.id").isEqualTo(order.id().intValue());
        assertThat(jsonContent).extractingJsonPathStringValue("@.bookIsbn").isEqualTo(order.bookIsbn());
        assertThat(jsonContent).extractingJsonPathStringValue("@.bookName").isEqualTo(order.bookName());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.bookPrice").isEqualTo(order.bookPrice());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.quantity").isEqualTo(order.quantity());
        assertThat(jsonContent).extractingJsonPathStringValue("@.status").isEqualTo(order.status().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.createdDate").isEqualTo(order.createdDate().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.lastModifiedDate").isEqualTo(order.lastModifiedDate().toString());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.version").isEqualTo(order.version());
    }

}
