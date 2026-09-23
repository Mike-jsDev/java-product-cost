package app;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {

            CustomerDao customerDao = context.getBean(CustomerDao.class);
            customerDao.createTable();
            customerDao.clear();
            System.out.println("Table customer is ready");

            System.out.println("\n=== SAVE ===");
            Customer alice = customerDao.save(
                    new Customer(null, "Alice Smith", "alice@example.com", "SSN-001")
            );
            Customer bob = customerDao.save(
                    new Customer(null, "Bob Johnson", "bob@example.com", "SSN-002")
            );
            System.out.println(alice);
            System.out.println(bob);

            System.out.println("\n=== FIND BY ID ===");
            System.out.println(customerDao.findById(alice.getId()));

            System.out.println("\n=== UPDATE ===");
            alice.setFullName("Alice Johnson");
            alice.setEmail("alice.johnson@example.com");
            System.out.println(customerDao.update(alice));

            System.out.println("\n=== FIND ALL ===");
            customerDao.findAll().forEach(System.out::println);

            System.out.println("\n=== DELETE ===");
            boolean deleted = customerDao.deleteById(bob.getId());
            System.out.println("Deleted Bob: " + deleted);

            System.out.println("\n=== FIND ALL AFTER DELETE ===");
            customerDao.findAll().forEach(System.out::println);
        }
    }
}
