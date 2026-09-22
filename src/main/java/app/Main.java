package app;

import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        StudentDao studentDao = new StudentDao();

        try {
            System.out.println("=== SAVE ===");
            Student student = new Student("Alice", "Smith", "alice@example.com");
            student.addHomework(new Homework("JDBC practice", LocalDate.now().plusDays(7), 90));
            student.addHomework(new Homework("Hibernate practice", LocalDate.now().plusDays(14), 95));
            studentDao.save(student);
            System.out.println("Saved: " + student);

            System.out.println("\n=== FIND BY EMAIL ===");
            Student foundByEmail = studentDao.findByEmail("alice@example.com");
            System.out.println(foundByEmail);
            System.out.println("Homeworks: " + foundByEmail.getHomeworks());

            System.out.println("\n=== FIND BY ID ===");
            Student foundById = studentDao.findById(foundByEmail.getId());
            System.out.println(foundById);

            System.out.println("\n=== UPDATE ===");
            foundById.setLastName("Johnson");
            Student updated = studentDao.update(foundById);
            System.out.println(updated);

            System.out.println("\n=== FIND ALL ===");
            List<Student> all = studentDao.findAll();
            all.forEach(System.out::println);

            System.out.println("\n=== DELETE ===");
            boolean deleted = studentDao.deleteById(updated.getId());
            System.out.println("Deleted: " + deleted);
            System.out.println("Find after delete: " + studentDao.findById(updated.getId()));

            System.out.println("\nAll checks passed");
        } finally {
            studentDao.close();
        }
    }
}
