package com.github.gluhov.service.user;

import com.github.gluhov.model.User;

import java.util.Optional;

public class UserTestData {
    public static final long USER_ID = 1;
    public static final long USER_NOT_FOUND_ID = 100;

    public static final User user1 = new User(USER_ID, "Dmitrii");
    public static final User user3 = new User(USER_ID + 2, "Oleg");

    public static Optional<User> getUpdated() { return Optional.of(new User(USER_ID, "Mike"));}
}