package app;

public class Main {

    public static void main(String[] args) {
        DataProvider provider = new DataProvider();
        Corrector corrector = new Corrector();

        try {
            String result = corrector.handleData(provider.getData());
            getOutput(result);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void getOutput(String output) {
        System.out.println(output);
    }
}
