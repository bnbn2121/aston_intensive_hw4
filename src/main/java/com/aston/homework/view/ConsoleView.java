package com.aston.homework.view;

import com.aston.homework.entity.User;
import com.aston.homework.service.UserService;
import com.aston.homework.service.UserServiceException;
import com.aston.homework.view.util.ConsoleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleView {
    private UserService userService;
    private boolean isExit = false;
    private static final Logger logger = LoggerFactory.getLogger(ConsoleView.class);

    public ConsoleView(UserService userService) {
        this.userService = userService;
    }

    public void runMenu() {
        logger.info("starting app");
        while (!isExit) {
            mainMenu();
        }
    }

    private void mainMenu() {
        showMenu();
        int input = ConsoleUtil.userIntInput(null, 0, 4);
        String response;
        try {
            response = switch (input) {
                case 1 -> addNewUser();
                case 2 -> showUserById();
                case 3 -> removeUserById();
                case 4 -> updateUserById();
                case 0 -> exitApp();
                default -> null;
            };
        } catch (UserServiceException e) {
            response = e.getMessage();
        }
        System.out.println();
        System.out.println(response);
        System.out.println();
        logger.debug(response);
    }

    private void showMenu() {
        System.out.println("=== Main menu ===");
        System.out.println("1. Add new user");
        System.out.println("2. Show user by id");
        System.out.println("3. Remove user by id");
        System.out.println("4. Update user by id");
        System.out.println("0. Exit");
        System.out.print("Your choice: ");
    }

    private String addNewUser() throws UserServiceException {
        String name = ConsoleUtil.userStringInput("Set user name:");
        String email = ConsoleUtil.userStringInput("Set user email:");
        int age = ConsoleUtil.userIntInput("Set user age:", 0, 130);
        User newUser = userService.createUser(name, email, age);
        userService.addUser(newUser);
        return "Success! User added with id = %d".formatted(newUser.getId());
    }

    private String showUserById() throws UserServiceException {
        int id = ConsoleUtil.userIntInput("Set user id:", 0, 1000);
        User user = userService.getUserById(id);
        return user.toString();
    }

    private String removeUserById() throws UserServiceException {
        int id = ConsoleUtil.userIntInput("Set user id to remove:", 0, 1000);
        if (userService.deleteUser(id)) {
            return "Success! User with id = %d is deleted".formatted(id);
        }
        return "The operation was unsuccessful";

    }

    private String updateUserById() throws UserServiceException {
        int id = ConsoleUtil.userIntInput("Set user id to update:", 0, 1000);
        System.out.println(userService.getUserById(id));
        System.out.println("Update user data please");
        String newName = ConsoleUtil.userStringInput("Set new user name:");
        String newEmail = ConsoleUtil.userStringInput("Set new user email:");
        int newAge = ConsoleUtil.userIntInput("Set new user age:", 0, 130);
        userService.updateUserById(id, newName, newEmail, newAge);
        return "Success! User with id=%d is updated".formatted(id);
    }

    private String exitApp() {
        isExit = true;
        logger.info("finishing app");
        return "Application is finished";
    }
}
