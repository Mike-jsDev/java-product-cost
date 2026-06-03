package app;

public class Main {

    public static void main(String[] args) {
        getOutput(new Corrector().handleData(
                new DataProvider().getData()
        ));
    }

    private static void getOutput(String output) {
        System.out.println(output);
    }
}