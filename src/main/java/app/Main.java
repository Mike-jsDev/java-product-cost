package app;

import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        DatabaseConnector connector = new DatabaseConnector();

        try {
            connector.connect();
            System.out.println("Connected to database company");

            EmployeeDAO employeeDAO = new EmployeeDAO(connector);
            employeeDAO.createTable();
            System.out.println("Table employees is ready");

            connector.executeUpdate("DELETE FROM employees");

            System.out.println("\n=== ADD ===");
            employeeDAO.addEmployee(new Employee("Alice", 28, "Developer", 2500));
            employeeDAO.addEmployee(new Employee("Bob", 35, "Manager", 3200));
            employeeDAO.addEmployee(new Employee("Carol", 24, "QA", 1800));
            List<Employee> employees = employeeDAO.getAllEmployees();
            printAll(employees);

            Employee bob = employees.get(1);
            System.out.println("\n=== UPDATE (id=" + bob.getId() + ") ===");
            bob.setAge(36);
            bob.setPosition("Senior Manager");
            bob.setSalary(3800);
            employeeDAO.updateEmployee(bob);
            System.out.println(employeeDAO.getEmployeeById(bob.getId()));

            Employee carol = employees.get(2);
            System.out.println("\n=== DELETE (id=" + carol.getId() + ") ===");
            employeeDAO.deleteEmployee(carol.getId());
            printAll(employeeDAO.getAllEmployees());

            Employee alice = employees.get(0);
            System.out.println("\n=== GET BY ID (id=" + alice.getId() + ") ===");
            System.out.println(employeeDAO.getEmployeeById(alice.getId()));

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            connector.close();
            System.out.println("\nConnection closed");
        }
    }

    private static void printAll(List<Employee> employees) {
        for (Employee employee : employees) {
            System.out.println(employee);
        }
    }
}
