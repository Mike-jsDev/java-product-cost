package app;

public class Plane implements Transport {

    @Override
    public void move() {
        System.out.println("Plane is flying in the sky");
    }
}
