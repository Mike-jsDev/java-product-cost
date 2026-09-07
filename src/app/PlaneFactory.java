package app;

public class PlaneFactory extends TransportFactory {

    @Override
    public Transport createTransport() {
        return new Plane();
    }
}
