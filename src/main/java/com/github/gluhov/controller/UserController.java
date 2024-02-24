package com.github.gluhov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gluhov.model.User;
import com.github.gluhov.repository.jpa.JpaUserRepository;
import com.github.gluhov.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/user", "/users"})
public class UserController extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService(new JpaUserRepository());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();
        if (path.endsWith("/user")) {
            String userIdParam = req.getParameter("id");
            if (userIdParam != null && !userIdParam.isEmpty()) {
                try {
                    Long id = Long.parseLong(userIdParam);
                    Optional<User> user = userService.getById(id);
                    if (user.isPresent()) {
                        resp.setContentType("application/json");
                        resp.setCharacterEncoding("UTF-8");
                        ObjectMapper objectMapper = new ObjectMapper();
                        String userJson = objectMapper.writeValueAsString(user.get());
                        resp.getWriter().write(userJson);
                    } else {
                        resp.sendError(HttpServletResponse.SC_NO_CONTENT, "No such user.");
                    }
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user id.");
                }
            }
        } else if (path.endsWith("/users")) {
            Optional<List<User>> users = userService.findAll();
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            if (users.isPresent()) {
                String usersJson = objectMapper.writeValueAsString(users.get());
                resp.getWriter().write(usersJson);
            }
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(req.getReader(), User.class);
        Optional<User> updatedUser = userService.update(user);
        if (updatedUser.isPresent()) {
            String userJson = objectMapper.writeValueAsString(updatedUser.get());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(userJson);
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
            String userJson = objectMapper.writeValueAsString(savedUser.get());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(userJson);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to save user.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userIdParam = req.getParameter("id");
        if (userIdParam != null && !userIdParam.isEmpty()) {
            try {
                Long id = Long.parseLong(userIdParam);
                userService.deleteById(id);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user id.");
            }
        }
    }
}
