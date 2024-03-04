package com.github.gluhov.controller;

import com.github.gluhov.repository.jpa.JpaEventRepository;
import com.github.gluhov.service.EventService;
import com.github.gluhov.to.EventTo;
import com.github.gluhov.util.ServletUtil;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;

@WebServlet("/api/v1/events/*")
public class EventRestControllerV1 extends HttpServlet {
    private EventService eventService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        eventService = new EventService(new JpaEventRepository());
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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