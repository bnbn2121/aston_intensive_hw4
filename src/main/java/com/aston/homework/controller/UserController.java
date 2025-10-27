package com.aston.homework.controller;

import com.aston.homework.dto.UserDtoIn;
import com.aston.homework.dto.UserDtoOut;
import com.aston.homework.service.UserService;
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
            UserDtoOut userDtoOut = userService.getUserById(id);
            return ResponseEntity.ok(userDtoOut);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserDtoIn userDtoIn) {
            UserDtoOut userDtoOut = userService.addUser(userDtoIn);
            return ResponseEntity.ok(userDtoOut);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable int id, @RequestBody UserDtoIn userDtoIn) {
            UserDtoOut userDtoOut = userService.updateUserById(id, userDtoIn);
            return ResponseEntity.ok(userDtoOut);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeUserById(@PathVariable int id) {
            boolean isDelete = userService.deleteUser(id);
            return ResponseEntity.ok("success delete: %s".formatted(isDelete));
    }

}
