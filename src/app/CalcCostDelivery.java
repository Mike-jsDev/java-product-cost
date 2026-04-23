package app;

public class CalcCostDelivery extends CalcCostBase {

    private static final double deliveryPrice = 7.0;

    @Override
    public double calcCost(Product product) {
        return super.calcCost(product) + deliveryPrice;
    }
}