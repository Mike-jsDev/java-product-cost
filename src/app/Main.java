package app;

public class Main {

    public static void main(String[] args) {

        String[] data = getData();

        Product product = new Product(
                data[0],
                Integer.parseInt(data[1]),
                Double.parseDouble(data[2])
        );

        CalcCostBase costBase = new CalcCostBase();
        double baseCost = costBase.calcCost(product);

        CalcCostDelivery costDelivery = new CalcCostDelivery();
        double deliveryCost = costDelivery.calcCost(product);

        StringBuilder baseBuilder = new StringBuilder();

        baseBuilder
                .append(product)
                .append("\nCost is ")
                .append(Constants.CURRENCY)
                .append(" ")
                .append(baseCost)
                .append(".");

        String baseOutput = baseBuilder.toString();

        StringBuilder deliveryBuilder = new StringBuilder();

        deliveryBuilder
                .append(product)
                .append("\nCost is ")
                .append(Constants.CURRENCY)
                .append(" ")
                .append(deliveryCost)
                .append(".");

        String deliveryOutput = deliveryBuilder.toString();

        getOutput(baseOutput);
        getOutput(deliveryOutput);
    }

    public static String[] getData() {
        return new String[]{"abc", "5", "2.5"};
    }

    public static void getOutput(String output) {
        System.out.println(output);
    }
}