package app;

public class Car implements Transport {

    @Override
    public void move() {
        System.out.println("Car is driving on the road");
    }
}
