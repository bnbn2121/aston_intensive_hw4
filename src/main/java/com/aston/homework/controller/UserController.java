package com.aston.homework.controller;

import com.aston.homework.dto.UserDtoIn;
import com.aston.homework.dto.UserDtoOut;
import com.aston.homework.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        try {
            UserDtoOut userDtoOut = userService.getUserById(id);
            return ResponseEntity.ok(userDtoOut);
        } catch (Exception e) {
            return sendResponseIfException(e);
        }
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserDtoIn userDtoIn) {
        try {
            UserDtoOut userDtoOut = userService.addUser(userDtoIn);
            return ResponseEntity.ok(userDtoOut);
        } catch (Exception e) {
            return sendResponseIfException(e);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable int id, @RequestBody UserDtoIn userDtoIn) {
        try {
            UserDtoOut userDtoOut = userService.updateUserById(id, userDtoIn);
            return ResponseEntity.ok(userDtoOut);
        } catch (Exception e) {
            return sendResponseIfException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeUserById(@PathVariable int id) {
        try {
            boolean isDelete = userService.deleteUser(id);
            return ResponseEntity.ok("success delete: %s".formatted(isDelete));
        } catch (Exception e) {
            return sendResponseIfException(e);
        }
    }

    private ResponseEntity<?> sendResponseIfException(Exception e) {
        HttpStatus status = null;
        if (e.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (e.getMessage().contains("email exists") || e.getMessage().contains("email already used")) {
            status = HttpStatus.CONFLICT;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(e.getMessage());
    }

}
