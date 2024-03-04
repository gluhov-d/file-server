package com.github.gluhov.service;

import com.github.gluhov.model.User;
import com.github.gluhov.repository.UserRepository;
import com.github.gluhov.to.UserTo;
import com.github.gluhov.util.UserUtil;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<UserTo> getById(Long id) {
        Optional<User> user = userRepository.getById(id);
        return user.map(UserUtil::createTo);
    }
    public void deleteById(Long id) { userRepository.deleteById(id); }
    public Optional<User> save(User user) { return userRepository.save(user); }
    public Optional<User> update(User user) { return userRepository.update(user); }
}