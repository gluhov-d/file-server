package com.github.gluhov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gluhov.model.User;
import com.github.gluhov.repository.jpa.JpaUserRepository;
import com.github.gluhov.service.UserService;
import com.github.gluhov.to.UserTo;
import com.github.gluhov.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;

@WebServlet("/api/v1/users/*")
public class UserRestControllerV1 extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService(new JpaUserRepository());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OptionalLong userId = ServletUtil.parseLongParam(req, resp);
        if (userId.isEmpty()) return;
        Optional<UserTo> userTo = userService.getById(userId.getAsLong());
        if (userTo.isPresent()) {
            ServletUtil.writeJsonResponse(resp, userTo.get());
        } else {
            resp.sendError(HttpServletResponse.SC_NO_CONTENT, "No such user.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(req.getReader(), User.class);
        Optional<User> updatedUser = userService.update(user);
        if (updatedUser.isPresent()) {
            ServletUtil.writeJsonResponse(resp, updatedUser.get());
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to save user.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(req.getReader(), User.class);
        Optional<User> savedUser = userService.save(user);
        if (savedUser.isPresent()) {
            ServletUtil.writeJsonResponse(resp, savedUser.get());
            resp.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to save user.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OptionalLong userId = ServletUtil.parseLongParam(req, resp);
        if (userId.isEmpty()) return;
        userService.deleteById(userId.getAsLong());
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}