package com.example.usermanagement.service;

import com.example.usermanagement.model.User;
import com.example.usermanagement.model.UserTree;
import com.example.usermanagement.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        return userRepository.save(user);
    }

    public User updateUser(String username, User updatedUser) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setEmail(updatedUser.getEmail());
                    user.setGender(updatedUser.getGender());
                    user.setPicture(updatedUser.getPicture());
                    user.setCountry(updatedUser.getCountry());
                    user.setState(updatedUser.getState());
                    user.setCity(updatedUser.getCity());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
    }


    private static final String RANDOM_USER_URL = "https://randomuser.me/api/?results=%d";

    private final List<User> users = new ArrayList<>();

    public List<User> generateUsers(int number) {
        RestTemplate restTemplate = new RestTemplate();
        String url = String.format(RANDOM_USER_URL, number);

        // Make the API request
        String response = restTemplate.getForObject(url, String.class);

        if (response != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode;
            try {
                rootNode = mapper.readTree(response);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            JsonNode resultsNode = rootNode.path("results");
            for (JsonNode userNode : resultsNode) {
                User user = new User();
                user.setName(userNode.path("name").path("first").asText() + " " +
                        userNode.path("name").path("last").asText());
                user.setUsername(userNode.path("login").path("username").asText());
                user.setEmail(userNode.path("email").asText());
                user.setGender(userNode.path("gender").asText());
                user.setCountry(userNode.path("location").path("country").asText());
                user.setState(userNode.path("location").path("state").asText());
                user.setCity(userNode.path("location").path("city").asText());
                user.setPicture(userNode.path("picture").path("large").asText());
                users.add(user);
            }
        }

        return users;
    }

    public List<UserTree> getUserTree() {
        // Generate a tree structure grouped by country, state, and city
        List<UserTree> tree = new ArrayList<>();

        for (User user : users) {
            addToTree(tree, user);
        }

        return tree;
    }

    private void addToTree(List<UserTree> tree, User user) {
        UserTree countryNode = findOrCreateNode(tree, user.getCountry(), null);
        UserTree stateNode = findOrCreateNode(countryNode.getChildren(), user.getState(), countryNode);
        findOrCreateNode(stateNode.getChildren(), user.getCity(), stateNode).getUsers().add(user);
    }

    private UserTree findOrCreateNode(List<UserTree> tree, String name, UserTree parent) {
        return tree.stream()
                .filter(node -> node.getName().equals(name))
                .findFirst()
                .orElseGet(() -> {
                    UserTree newNode = new UserTree(name, parent);
                    tree.add(newNode);
                    return newNode;
                });
    }
}