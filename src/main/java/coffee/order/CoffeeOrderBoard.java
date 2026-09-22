package coffee.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CoffeeOrderBoard {

    private static final Logger log = LoggerFactory.getLogger(CoffeeOrderBoard.class);

    private final List<Order> orders = new ArrayList<>();
    private int nextOrderNumber = 1;

    public Order add(String name) {
        if (name == null || name.isBlank()) {
            IllegalArgumentException exception =
                    new IllegalArgumentException("Customer name must not be null or blank");
            log.error("Failed to add order: invalid customer name '{}'", name, exception);
            throw exception;
        }

        Order order = new Order(nextOrderNumber++, name.trim());
        orders.add(order);
        log.info("Order added: number={}, name={}", order.getNumber(), order.getName());
        log.debug("Queue size after add: {}", orders.size());
        return order;
    }

    public Order deliver() {
        if (orders.isEmpty()) {
            IllegalStateException exception =
                    new IllegalStateException("Cannot deliver: order queue is empty");
            log.error("Deliver failed: queue is empty", exception);
            throw exception;
        }

        Order order = orders.remove(0);
        log.info("Order delivered from queue head: number={}, name={}",
                order.getNumber(), order.getName());
        log.debug("Queue size after deliver: {}", orders.size());
        return order;
    }

    public Order deliver(int number) {
        if (number <= 0) {
            IllegalArgumentException exception =
                    new IllegalArgumentException("Order number must be a positive natural number");
            log.error("Deliver failed: invalid order number {}", number, exception);
            throw exception;
        }

        Iterator<Order> iterator = orders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.getNumber() == number) {
                iterator.remove();
                log.info("Order delivered by number: number={}, name={}",
                        order.getNumber(), order.getName());
                log.debug("Queue size after deliver({}): {}", number, orders.size());
                return order;
            }
        }

        IllegalArgumentException exception =
                new IllegalArgumentException("Order with number " + number + " was not found");
        log.error("Deliver failed: order {} not found in queue", number, exception);
        throw exception;
    }

    public void draw() {
        log.info("Current order board state ({} orders)", orders.size());
        System.out.println("Num | Name");
        for (Order order : orders) {
            System.out.println(order);
        }
    }

    public int getOrderCount() {
        return orders.size();
    }
}
