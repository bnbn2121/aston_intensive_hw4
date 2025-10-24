package com.aston.homework.view.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleUtilTest {
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    private void setInput(String inputData) {
        System.setIn(new ByteArrayInputStream(inputData.getBytes()));
        resetConsoleUtilScanner();
    }

    private void resetConsoleUtilScanner() {
        try {
            Field scannerField = ConsoleUtil.class.getDeclaredField("scanner");
            scannerField.setAccessible(true);
            scannerField.set(null, new Scanner(System.in));
        } catch (Exception e) {
            throw new RuntimeException("reflection error", e);
        }
    }

    private String getOutput() {
        return outputStream.toString();
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void shouldReturnStringInput() {
        // Given
        String input = "test input\n";
        setInput(input);

        // When
        String result = ConsoleUtil.userStringInput("test message");

        // Then
        assertEquals("test input", result);
        assertTrue(getOutput().contains("test message"));
    }

    @Test
    void shouldReturnStringInputWithNullMessage() {
        // Given
        String input = "test input\n";
        setInput(input);

        // When
        String result = ConsoleUtil.userStringInput(null);

        // Then
        assertEquals("test input", result);
        assertTrue(getOutput().isEmpty());
    }

    @Test
    void shouldReturnIntegerInputCorrect() {
        // Given
        String input = "25\n";
        setInput(input);

        // When
        int result = ConsoleUtil.userIntInput("test message", 0, 100);

        // Then
        assertEquals(25, result);
        assertTrue(getOutput().contains("test message"));
    }
    @Test
    void shouldReturnIntegerInputCorrectWithNullMessage() {
        // Given
        String input = "25\n";
        setInput(input);

        // When
        int result = ConsoleUtil.userIntInput(null, 0, 100);

        // Then
        assertEquals(25, result);
        assertTrue(getOutput().isEmpty());
    }

    @Test
    void shouldReturnIntegerInputCorrectWithAttemptStringInput() {
        // Given
        String input = "test string input\n25\n";
        setInput(input);

        // When
        int result = ConsoleUtil.userIntInput(null, 0, 100);

        // Then
        assertEquals(25, result);
        assertTrue(getOutput().contains("input must be number"));
    }

    @Test
    void shouldReturnIntegerInputCorrectWithAttemptInvalidNumber() {
        // Given
        String input = "999\n-55\n25\n";
        setInput(input);

        // When
        int result = ConsoleUtil.userIntInput(null, 0, 100);

        // Then
        assertEquals(25, result);
        assertTrue(getOutput().contains("input must be number from"));
    }

}
