package com.github.gluhov.controller;

import com.github.gluhov.repository.jpa.JpaEventRepository;
import com.github.gluhov.service.EventService;
import com.github.gluhov.to.EventTo;
import com.github.gluhov.util.ServletUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;

@Path("/storage/api/v1/events/")
@Produces("application/json")
@Tag(name = "Events", description = "Operations with events")
@WebServlet("/api/v1/events/*")
public class EventRestControllerV1 extends HttpServlet {
    private EventService eventService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        eventService = new EventService(new JpaEventRepository());
    }

    @GET
    @Path("/{id}")
    @Operation(
            summary = "Get event by ID",
            description = "Returns an event by its ID",
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            description = "The ID of the event",
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
                            description = "Event not found",
                            responseCode = "404",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    @Override
    public void doGet(@Parameter(hidden = true) HttpServletRequest req, @Parameter(hidden = true) HttpServletResponse resp) throws ServletException, IOException {
        OptionalLong eventId = ServletUtil.parseLongParam(req, resp);
        if (eventId.isEmpty()) return;
        Optional<EventTo> eventTo = eventService.getById(eventId.getAsLong());
        if (eventTo.isPresent()) {
            ServletUtil.writeJsonResponse(resp, eventTo.get());
        } else {
            resp.sendError(HttpServletResponse.SC_NO_CONTENT, "No such event.");
        }
    }
}