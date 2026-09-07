package app;

import com.example.PasswordGenerator;

public class Main {

    public static void main(String[] args) {
        int passwordLength = 12;
        String generatedPassword = PasswordGenerator.generatePassword(passwordLength);
        System.out.println("Згенерований пароль: " + generatedPassword);
    }
}
