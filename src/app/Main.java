package app;

public class Main {

    public static void main(String[] args) {
        User user = new User("Alice");
        Address address = new Address("Khreshchatyk 1", "Kyiv", "Ukraine");

        user.setAddress(address);

        System.out.println("User: " + user.getName());
        System.out.println("Address: " + user.getAddress());
    }
}
