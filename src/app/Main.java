package app;

public class Main {

    public static void main(String[] args) {
        Logger logger1 = Logger.getInstance();
        Logger logger2 = Logger.getInstance();

        System.out.println("Same instance: " + (logger1 == logger2));

        logger1.log("Application started");
        doWork();
        logger2.log("Application finished");

        System.out.println("Total logs: " + logger1.getLogs().size());
        System.out.println("All logs: " + logger1.getLogs());
    }

    private static void doWork() {
        Logger logger = Logger.getInstance();
        logger.log("Processing data...");
        logger.log("Data processed successfully");
    }
}
