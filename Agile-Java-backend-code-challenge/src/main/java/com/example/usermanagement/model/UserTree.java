package com.example.usermanagement.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserTree {
    private final String name;
    private final List<UserTree> children = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final UserTree parent;

    public UserTree(String name, UserTree parent) {
        this.name = name;
        this.parent = parent;
    }

}
