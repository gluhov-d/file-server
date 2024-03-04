package com.github.gluhov.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.OptionalLong;

public class ServletUtil {
    public static OptionalLong parseLongParam(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing path info.");
            return OptionalLong.empty();
        }
        String[] pathParts = pathInfo.split("/");
        try {
            return OptionalLong.of(Long.parseLong(pathParts[1]));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format.");
            return OptionalLong.empty();
        }
    }

    public static void writeJsonResponse(HttpServletResponse resp, Object object) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(object);
        resp.getWriter().write(json);
    }
}