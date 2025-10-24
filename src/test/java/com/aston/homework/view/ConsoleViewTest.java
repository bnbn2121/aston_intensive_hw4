package com.aston.homework.view;

import com.aston.homework.entity.User;
import com.aston.homework.service.UserService;
import com.aston.homework.view.util.ConsoleUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsoleViewTest {
    private ConsoleView consoleView;
    private MockedStatic<ConsoleUtil> consoleUtilMockedStatic;
    private ByteArrayOutputStream outputStream;
    private final PrintStream originalOut = System.out;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        consoleView = new ConsoleView(userService);
        consoleUtilMockedStatic = Mockito.mockStatic(ConsoleUtil.class);

        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        consoleUtilMockedStatic.close();
    }

    @Test
    void shouldShowMenuCorrectly() {
        //Given
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput(null, 0, 4)).thenReturn(0);

        //When
        consoleView.runMenu();

        //Then
        String output = outputStream.toString();
        assertTrue(output.contains("=== Main menu ==="));
        assertTrue(output.contains("1. Add new user"));
        assertTrue(output.contains("2. Show user by id"));
        assertTrue(output.contains("3. Remove user by id"));
        assertTrue(output.contains("4. Update user by id"));
        assertTrue(output.contains("0. Exit"));
        assertTrue(output.contains("Your choice: "));
    }

    @Test
    void shouldAddNewUser() throws Exception {
        //Given
        String name = "testName";
        String email = "testEmail@email.ru";
        int age = 25;

        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput(null, 0, 4)).thenReturn(1).thenReturn(0);
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userStringInput("Set user name:")).thenReturn(name);
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userStringInput("Set user email:")).thenReturn(email);
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput("Set user age:", 0, 130)).thenReturn(age);
        User newUser = new User(name, email, age);
        when(userService.createUser(name, email, age)).thenReturn(newUser);
        when(userService.addUser(newUser)).thenReturn(newUser);

        //When
        consoleView.runMenu();

        //Then
        String output = outputStream.toString();
        assertTrue(output.contains("Success! User added with id ="));
        verify(userService).createUser(name, email, age);
        verify(userService).addUser(newUser);
    }

    @Test
    void shouldShowUserById() throws Exception {
        //Given
        int id = 1;
        String name = "testName";
        String email = "testEmail@email.ru";
        int age = 25;

        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput(null, 0, 4)).thenReturn(2).thenReturn(0);
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput("Set user id:", 0, 1000)).thenReturn(id);
        User user = new User(name, email, age);
        when(userService.getUserById(id)).thenReturn(user);

        //When
        consoleView.runMenu();

        //Then
        String output = outputStream.toString();
        assertTrue(output.contains(user.toString()));
        verify(userService).getUserById(id);
    }

    @Test
    void shouldRemoveUserByIdSuccess() throws Exception {
        //Given
        int id = 1;

        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput(null, 0, 4)).thenReturn(3).thenReturn(0);
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput("Set user id to remove:", 0, 1000)).thenReturn(id);
        when(userService.deleteUser(id)).thenReturn(true);

        //When
        consoleView.runMenu();

        //Then
        String output = outputStream.toString();
        assertTrue(output.contains("Success! User with id = %d is deleted".formatted(id)));
        verify(userService).deleteUser(id);
    }

    @Test
    void shouldRemoveUserByIdUnsuccess() throws Exception {
        //Given
        int id = 1;

        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput(null, 0, 4)).thenReturn(3).thenReturn(0);
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput("Set user id to remove:", 0, 1000)).thenReturn(id);
        when(userService.deleteUser(id)).thenReturn(false);

        //When
        consoleView.runMenu();

        //Then
        String output = outputStream.toString();
        assertTrue(output.contains("The operation was unsuccessful"));
        verify(userService).deleteUser(id);
    }

    @Test
    void shouldUpdateUserById() throws Exception {
        //Given
        int id = 1;
        String newName = "testName";
        String newEmail = "testEmail@email.ru";
        int newAge = 25;

        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput(null, 0, 4)).thenReturn(4).thenReturn(0);
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput("Set user id to update:", 0, 1000)).thenReturn(id);
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userStringInput("Set new user name:")).thenReturn(newName);
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userStringInput("Set new user email:")).thenReturn(newEmail);
        consoleUtilMockedStatic.when(() -> ConsoleUtil.userIntInput("Set new user age:", 0, 130)).thenReturn(newAge);
        when(userService.updateUserById(id, newName, newEmail, newAge)).thenReturn(true);

        //When
        consoleView.runMenu();

        //Then
        String output = outputStream.toString();
        assertTrue(output.contains("Success! User with id=%d is updated".formatted(id)));
        verify(userService).updateUserById(id, newName, newEmail, newAge);
    }

}
