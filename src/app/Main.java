package app;

import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        UserRepository repository = new UserRepository();

        Optional<User> userById = repository.findUserById(2);
        if (userById.isPresent()) {
            System.out.println("User found by id: " + userById.get());
        } else {
            System.out.println("User with this id was not found");
        }

        Optional<User> userByEmail = repository.findUserByEmail("alice@example.com");
        if (userByEmail.isPresent()) {
            System.out.println("User found by email: " + userByEmail.get());
        } else {
            System.out.println("User with this email was not found");
        }

        Optional<List<User>> allUsers = repository.findAllUsers();
        if (allUsers.isPresent()) {
            System.out.println("Users count: " + allUsers.get().size());
        } else {
            System.out.println("User list is empty");
        }
    }
}
