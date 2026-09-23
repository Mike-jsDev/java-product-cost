package app;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {

    private final Map<Long, Order> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Order findById(Long id) {
        Order order = storage.get(id);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id " + id + " was not found");
        }
        return order;
    }

    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Order add(Order order) {
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one product");
        }

        order.setId(idGenerator.getAndIncrement());
        if (order.getCreationDate() == null) {
            order.setCreationDate(LocalDateTime.now());
        }
        order.setTotalCost(calculateTotal(order.getProducts()));
        storage.put(order.getId(), order);
        return order;
    }

    private BigDecimal calculateTotal(List<Product> products) {
        return products.stream()
                .map(Product::getCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
