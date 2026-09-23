package app;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public class OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    public OrderRepository(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    public Order findById(Long id) {
        return orderJpaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order with id " + id + " was not found"));
    }

    public List<Order> findAll() {
        return orderJpaRepository.findAll();
    }

    public Order add(Order order) {
        validate(order);
        order.setId(null);
        order.recalculateTotalCost();
        return orderJpaRepository.save(order);
    }

    public Order update(Long id, Order updatedOrder) {
        Order existing = findById(id);
        validate(updatedOrder);

        existing.getProducts().clear();
        existing.getProducts().addAll(updatedOrder.getProducts());
        existing.recalculateTotalCost();
        if (updatedOrder.getCreatedAt() != null) {
            existing.setCreatedAt(updatedOrder.getCreatedAt());
        }
        return orderJpaRepository.save(existing);
    }

    public void deleteById(Long id) {
        if (!orderJpaRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Order with id " + id + " was not found");
        }
        orderJpaRepository.deleteById(id);
    }

    private void validate(Order order) {
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Order must contain at least one product");
        }
        for (Product product : order.getProducts()) {
            if (product.getName() == null || product.getName().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Product name must not be blank");
            }
            if (product.getPrice() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Product price must not be null");
            }
        }
    }
}
