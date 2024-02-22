package com.github.gluhov.service;

import com.github.gluhov.model.User;
import com.github.gluhov.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> getById(Long id) { return userRepository.getById(id); }
    public void deleteById(Long id) { userRepository.deleteById(id); }
    public Optional<User> save(User user) { return userRepository.save(user); }
    public Optional<User> update(User user) { return userRepository.update(user); }
    public Optional<List<User>> findAll() { return userRepository.findAll(); }
    public Boolean checkIfExist(Long id) { return userRepository.checkIfExist(id); }
}