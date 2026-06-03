package app;

public class CalcCostDelivery extends CalcCostBase {

    private static final double DELIVERY_PRICE = 7.0;

    @Override
    public double calcCost(Product product) {
        return super.calcCost(product) + DELIVERY_PRICE;
    }
}