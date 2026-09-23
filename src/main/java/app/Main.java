package app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        ProductRepository productRepository = context.getBean(ProductRepository.class);
        Cart cart = context.getBean(Cart.class);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        printHelp();

        while (running) {
            System.out.print("\nChoose option: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> printProducts(productRepository);
                    case "2" -> addToCart(scanner, cart);
                    case "3" -> removeFromCart(scanner, cart);
                    case "4" -> printCart(cart);
                    case "5" -> {
                        cart = context.getBean(Cart.class);
                        System.out.println("New Cart bean created from context (prototype scope).");
                        printCart(cart);
                    }
                    case "0" -> running = false;
                    default -> System.out.println("Unknown option. Try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        context.close();
        System.out.println("Bye!");
    }

    private static void printHelp() {
        System.out.println("""
                === Cart console ===
                1 - Show products
                2 - Add product to cart by id
                3 - Remove product from cart by id
                4 - Show cart
                5 - Get new Cart bean from context
                0 - Exit
                """);
    }

    private static void printProducts(ProductRepository productRepository) {
        System.out.println("Products:");
        productRepository.findAll().forEach(System.out::println);
    }

    private static void addToCart(Scanner scanner, Cart cart) {
        System.out.print("Enter product id: ");
        Long id = Long.valueOf(scanner.nextLine().trim());
        cart.addProduct(id);
        System.out.println("Product added to cart.");
    }

    private static void removeFromCart(Scanner scanner, Cart cart) {
        System.out.print("Enter product id: ");
        Long id = Long.valueOf(scanner.nextLine().trim());
        boolean removed = cart.removeProduct(id);
        System.out.println(removed ? "Product removed from cart." : "Product was not in cart.");
    }

    private static void printCart(Cart cart) {
        System.out.println("Cart items:");
        if (cart.getItems().isEmpty()) {
            System.out.println("(empty)");
        } else {
            cart.getItems().forEach(System.out::println);
            System.out.println("Total: " + cart.getTotal());
        }
    }
}
