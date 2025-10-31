package com.aston.homework.controller;

import com.aston.homework.dto.EventName;
import com.aston.homework.dto.UserDtoIn;
import com.aston.homework.dto.UserDtoOut;
import com.aston.homework.service.UserService;
import com.aston.homework.service.impl.KafkaProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class UserController {
    private final UserService userService;
    private final KafkaProducerService kafkaProducerService;

    public UserController(UserService userService, KafkaProducerService kafkaProducerService) {
        this.userService = userService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        UserDtoOut userDtoOut = userService.getUserById(id);
        return ResponseEntity.ok(userDtoOut);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserDtoIn userDtoIn) {
        UserDtoOut userDtoOut = userService.addUser(userDtoIn);
        kafkaProducerService.sendEvent(EventName.CREATE, userDtoOut);
        return ResponseEntity.ok(userDtoOut);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable int id, @RequestBody UserDtoIn userDtoIn) {
        UserDtoOut userDtoOut = userService.updateUserById(id, userDtoIn);
        return ResponseEntity.ok(userDtoOut);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeUserById(@PathVariable int id) {
        UserDtoOut userDtoOut = userService.getUserById(id);
        boolean isDelete = userService.deleteUser(id);
        kafkaProducerService.sendEvent(EventName.DELETE, userDtoOut);
        return ResponseEntity.ok("success delete: %s".formatted(isDelete));
    }

}
