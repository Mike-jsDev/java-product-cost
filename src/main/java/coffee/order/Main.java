package coffee.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Coffee order board demo started");

        CoffeeOrderBoard board = new CoffeeOrderBoard();

        board.add("Alen");
        board.add("Yoda");
        board.add("Obi-van");
        board.add("John Snow");

        System.out.println("\n=== Board after adding orders ===");
        board.draw();

        System.out.println("\n=== Deliver next order ===");
        Order delivered = board.deliver();
        System.out.println("Delivered: " + delivered);

        System.out.println("\n=== Deliver order #3 (ready earlier) ===");
        Order readyEarlier = board.deliver(3);
        System.out.println("Delivered: " + readyEarlier);

        System.out.println("\n=== Board after deliveries ===");
        board.draw();

        try {
            board.deliver(99);
        } catch (IllegalArgumentException e) {
            log.error("Expected error while delivering missing order", e);
        }

        log.info("Coffee order board demo finished");
    }
}
