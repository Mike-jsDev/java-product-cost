package app;

import java.util.Scanner;

public class Main {

    static double balance;

    public static void main(String[] args) {
        balance = getBalance();

        try {
            double amount = getAmount();
            validateAmount(balance, amount);
            balance = getBalance(balance, amount);
            System.out.printf("Funds are OK. Purchase paid.%nBalance is USD %.2f", balance);
        } catch (InvalidInputException | FundsException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static double getBalance() {
        return 1000.00;
    }

    private static double getAmount() {
        System.out.printf("Balance is USD %.2f.%n" +
                "Enter purchase amount, USD: ", balance);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Amount must be a number!");
        }
    }

    private static void validateAmount(double balance, double withdrawal) {
        if (withdrawal <= 0) {
            throw new InvalidInputException("Amount must be greater than zero!");
        }
        if (withdrawal > balance) {
            throw new FundsException("Insufficient funds!");
        }
    }

    private static double getBalance(double balance, double withdrawal) {
        return balance - withdrawal;
    }
}
