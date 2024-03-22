package com.github.gluhov.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;

@WebServlet(name = "SwaggerUIServlet", urlPatterns = {"/swagger-ui/*"})
public class SwaggerUIControllerV1 extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String resourcePath = request.getPathInfo();

        if (resourcePath == null || resourcePath.equals("/")) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String realPath = getServletContext().getRealPath("/WEB-INF/classes/swagger/" + resourcePath);
        File file = new File(realPath);

        if (!file.exists() || file.isDirectory()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (InputStream resourceStream = new FileInputStream(file);
             OutputStream outputStream = response.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = resourceStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
