package com.example.usermanagement.controller;

import com.example.usermanagement.model.User;
import com.example.usermanagement.model.UserTree;
import com.example.usermanagement.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{username}/")
    public User getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @PostMapping("/")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{username}/")
    public User updateUser(@PathVariable String username, @RequestBody User user) {
        return userService.updateUser(username, user);
    }

    @DeleteMapping("/{username}/")
    public void deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
    }

    @GetMapping("/generate/{number}/")
    public List<User> generateUsers(@PathVariable int number) {
        return userService.generateUsers(number);
    }

    @GetMapping("/tree/")
    public List<UserTree> getUserTree() {
        return userService.getUserTree();
    }
}