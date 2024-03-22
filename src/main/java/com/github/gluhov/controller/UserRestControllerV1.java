package com.github.gluhov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gluhov.model.User;
import com.github.gluhov.repository.jpa.JpaUserRepository;
import com.github.gluhov.service.UserService;
import com.github.gluhov.to.EventTo;
import com.github.gluhov.to.UserTo;
import com.github.gluhov.util.ServletUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;

@Path("/storage/api/v1/users/")
@Produces("application/json")
@Tag(name = "Users", description = "Operations with users")
@WebServlet("/api/v1/users/*")
public class UserRestControllerV1 extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService(new JpaUserRepository());
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Get user by ID",
            description = "Returns an user by its ID",
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            description = "The ID of the user",
                            required = true,
                            name = "id",
                            schema = @Schema(implementation = Long.class)
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = false,
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "Successful operation",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = UserTo.class))
                    ),
                    @ApiResponse(
                            description = "User not found",
                            responseCode = "404",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    @Override
    public void doGet(@Parameter(hidden = true) HttpServletRequest req, @Parameter(hidden = true) HttpServletResponse resp) throws ServletException, IOException {
        OptionalLong userId = ServletUtil.parseLongParam(req, resp);
        if (userId.isEmpty()) return;
        Optional<UserTo> userTo = userService.getById(userId.getAsLong());
        if (userTo.isPresent()) {
            ServletUtil.writeJsonResponse(resp, userTo.get());
        } else {
            resp.sendError(HttpServletResponse.SC_NO_CONTENT, "No such user.");
        }
    }

    @PUT
    @Operation(
            summary = "Update user",
            description = "Updates an existing user with the provided details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful update",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Failed to update user"
                    )
            }
    )
    @Override
    public void doPut(@Parameter(hidden = true) HttpServletRequest req, @Parameter(hidden = true) HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.readValue(req.getReader(), User.class);
        Optional<User> updatedUser = userService.update(user);
        if (updatedUser.isPresent()) {
            ServletUtil.writeJsonResponse(resp, updatedUser.get());
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to save user.");
        }
    }

    @POST
    @Operation(
            summary = "Create new user",
            description = "Creates a new user with the provided details",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Failed to save user"
                    )
            }
    )
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req, @Parameter(hidden = true) HttpServletResponse resp) throws ServletException, IOException {
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

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Delete user by ID",
            description = "Returns delete operation status",
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            description = "The ID of the user",
                            required = true,
                            name = "id",
                            schema = @Schema(implementation = Long.class)
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = false,
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "Successful operation",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = EventTo.class))
                    ),
                    @ApiResponse(
                            description = "User not found",
                            responseCode = "404",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    @Override
    public void doDelete(@Parameter(hidden = true) HttpServletRequest req, @Parameter(hidden = true) HttpServletResponse resp) throws ServletException, IOException {
        OptionalLong userId = ServletUtil.parseLongParam(req, resp);
        if (userId.isEmpty()) return;
        userService.deleteById(userId.getAsLong());
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}