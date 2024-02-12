package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    Collection<User> getAllUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(long userId);

    void deleteUser(long userId);
}
