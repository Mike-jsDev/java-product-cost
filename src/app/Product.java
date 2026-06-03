package app;

public class Product {

    private String name;
    private int quota;
    private double price;

    public Product(String name, int quota, double price) {
        this.name = name;
        this.quota = quota;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getQuota() {
        return quota;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        StringBuilder massageBuilder = new StringBuilder();

        massageBuilder
                .append("Product: ")
                .append(name)
                .append(", quota is ")
                .append(quota)
                .append(" ")
                .append(Constants.MEASURE)
                .append(", price is ")
                .append(Constants.CURRENCY)
                .append(" ")
                .append(price)
                .append(".");

        return massageBuilder.toString();
    }
}