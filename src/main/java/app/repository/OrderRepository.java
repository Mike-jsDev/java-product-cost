package app.repository;

import app.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class OrderRepository {

    private final Map<Long, Order> storage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGenerator.getAndIncrement());
        }
        storage.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Order> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Order update(Order order) {
        if (order.getId() == null || !storage.containsKey(order.getId())) {
            throw new IllegalArgumentException("Order with id " + order.getId() + " was not found");
        }
        storage.put(order.getId(), order);
        return order;
    }

    public boolean deleteById(Long id) {
        return storage.remove(id) != null;
    }

    public void clear() {
        storage.clear();
        idGenerator.set(1);
    }
}
