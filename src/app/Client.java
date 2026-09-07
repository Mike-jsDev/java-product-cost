package app;

public class Client {

    public void run() {
        TransportFactory carFactory = new CarFactory();
        TransportFactory planeFactory = new PlaneFactory();

        Transport car = carFactory.createTransport();
        Transport plane = planeFactory.createTransport();

        System.out.println("Created by CarFactory: " + car.getClass().getSimpleName());
        System.out.println("Created by PlaneFactory: " + plane.getClass().getSimpleName());

        car.move();
        plane.move();
    }
}
