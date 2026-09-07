package app;

public class Main {

    public static void main(String[] args) {
        DataHandler dataHandler = new DataHandler();
        MyThread myThread1 = new MyThread("Thread 1", dataHandler);
        MyThread myThread2 = new MyThread("Thread 2", dataHandler);
        myThread1.start();
        myThread2.start();
    }
}
