package com.veerendra.library;

import java.util.Scanner;

public final class InputHelper {
    private InputHelper() {}

    public static String readText(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
