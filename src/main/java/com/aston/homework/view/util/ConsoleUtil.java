package com.aston.homework.view.util;

import java.util.Scanner;

public class ConsoleUtil {
    private static Scanner scanner = new Scanner(System.in);

    private ConsoleUtil(){}

    public static String userStringInput(String message) {
        if (message != null) {
            System.out.println(message);
        }
        return scanner.nextLine();
    }

    public static int userIntInput(String message, int minValue, int maxValue) {
        if (message != null) {
            System.out.println(message);
        }
        boolean isValidInput = false;
        int input = 0;
        while (!isValidInput) {
            if (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.println("input must be number");
                continue;
            }
            input = scanner.nextInt();
            scanner.nextLine();
            if (input<minValue || input> maxValue) {
                System.out.println("input must be number from %d till %d".formatted(minValue, maxValue));
                continue;
            }
            isValidInput = true;
        }
        return input;
    }

    public static void close() {
        scanner.close();
    }
}
