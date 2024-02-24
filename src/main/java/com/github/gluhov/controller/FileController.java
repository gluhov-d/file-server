package com.github.gluhov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gluhov.model.FileData;
import com.github.gluhov.model.FileStatus;
import com.github.gluhov.model.User;
import com.github.gluhov.repository.jpa.JpaFileDataDataRepository;
import com.github.gluhov.repository.jpa.JpaUserRepository;
import com.github.gluhov.service.FileDataService;
import com.github.gluhov.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@WebServlet(urlPatterns = {"/user/*"})
public class FileController extends HttpServlet {
    private FileDataService fileDataService;
    private UserService userService;

    private final int FILE_MAX_SIZE = 100 * 1024;
    private final int MEM_MAX_SIZE = 100 * 1024;
    private final String directory = "/Users/dmitriiglukhov/Documents/GIT/file-server/src/main/resources/uploads";

    @Override
    public void init() throws ServletException {
        super.init();
        fileDataService = new FileDataService(new JpaFileDataDataRepository());
        userService = new UserService(new JpaUserRepository());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length != 4 || "file".equals(pathParts[2])) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
            return;
        }
        long userId;
        long fileId;
        try {
            userId = Long.parseLong(pathParts[1]);
            fileId = Long.parseLong(pathParts[3]);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
            return;
        }

        Optional<User> userOpt = userService.getById(userId);
        Optional<FileData> fileDataOpt = fileDataService.getById(fileId);
        if (userOpt.isEmpty() || fileDataOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No such file or user.");
            return;
        }
        FileData fileData = fileDataOpt.get();

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileData.getName() + "\"");
        resp.setCharacterEncoding("UTF-8");
        File file = new File(fileDataOpt.get().getFilePath());
        try (InputStream in = new FileInputStream(file); ServletOutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
        } catch (IOException e) {
            throw new ServletException("Error in downloading file.", e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length != 3 || !"file".equals(pathParts[2])) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format.");
            return;
        }

        long userId;
        try {
            userId = Long.parseLong(pathParts[1]);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format.");
            return;
        }

        Optional<User> userOpt = userService.getById(userId);
        if (userOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found.");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        FileData fileData = objectMapper.readValue(req.getReader(), FileData.class);

        Optional<FileData> updated = fileDataService.update(fileData);

        if (updated.isPresent()) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(updated.get()));
        } else {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error saving file data.");
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getContentType().toLowerCase(Locale.ENGLISH).startsWith("multipart/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Form must have enctype=multipart/form-data.");
            return;
        }

        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length != 3 || !"file".equals(pathParts[2])) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format.");
            return;
        }

        long userId;
        try {
            userId = Long.parseLong(pathParts[1]);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user ID format.");
            return;
        }

        Optional<User> userOpt = userService.getById(userId);
        if (userOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found.");
            return;
        }
        User user = userOpt.get();

        DiskFileItemFactory factory = new DiskFileItemFactory.Builder()
                .setBufferSize(MEM_MAX_SIZE)
                .setPath(Path.of(directory)).get();

        JakartaServletDiskFileUpload upload = new JakartaServletDiskFileUpload(factory);
        upload.setFileSizeMax(FILE_MAX_SIZE);

        try {
            List<DiskFileItem> formItems = upload.parseRequest(req);

            if (formItems != null && formItems.size() > 0) {
                for (DiskFileItem item : formItems) {
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        String filePath = directory + File.separator + fileName;
                        File storeFile = new File(filePath);
                        Optional<FileData> savedFileData = fileDataService.save(new FileData(fileName, filePath, FileStatus.AVAILABLE), user);
                        if (savedFileData.isPresent()) {
                            item.write(storeFile.toPath());
                            resp.setContentType("application/json");
                            resp.setCharacterEncoding("UTF-8");
                            resp.setStatus(HttpServletResponse.SC_CREATED);
                            ObjectMapper objectMapper = new ObjectMapper();
                            resp.getWriter().write(objectMapper.writeValueAsString(savedFileData.get()));
                        } else {
                            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error saving file.");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error uploading file: " + ex.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length != 4 || "file".equals(pathParts[2])) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid URL format");
            return;
        }
        long userId;
        long fileId;
        try {
            userId = Long.parseLong(pathParts[1]);
            fileId = Long.parseLong(pathParts[3]);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid ID format");
            return;
        }

        Optional<User> userOpt = userService.getById(userId);
        if (userOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No such user.");
            return;
        }
        fileDataService.deleteById(fileId);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
