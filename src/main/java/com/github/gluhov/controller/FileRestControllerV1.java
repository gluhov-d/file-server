package com.github.gluhov.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gluhov.model.FileEntity;
import com.github.gluhov.model.FileStatus;
import com.github.gluhov.repository.jpa.JpaEventRepository;
import com.github.gluhov.repository.jpa.JpaFileEntityRepository;
import com.github.gluhov.repository.jpa.JpaUserRepository;
import com.github.gluhov.service.FileEntityService;
import com.github.gluhov.to.FileTo;
import com.github.gluhov.util.ServletUtil;
import jakarta.servlet.ServletContext;
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
import java.util.OptionalLong;

@WebServlet("/api/v1/files/*")
public class FileRestControllerV1 extends HttpServlet {
    private FileEntityService fileEntityService;
    private final int FILE_MAX_SIZE = 100 * 1024;
    private final int MEM_MAX_SIZE = 100 * 1024;
    private String UPLOAD_PATH;

    @Override
    public void init() throws ServletException {
        super.init();
        fileEntityService = new FileEntityService(new JpaFileEntityRepository(), new JpaEventRepository(), new JpaUserRepository());
        ServletContext context = getServletContext();
        String relativePath = "resources/uploads";
        UPLOAD_PATH = context.getRealPath(relativePath);

        File uploadDir = new File(UPLOAD_PATH);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new ServletException("Can not get path to upload directory: " + UPLOAD_PATH);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OptionalLong fileId = ServletUtil.parseLongParam(req, resp);
        if (fileId.isEmpty()) return;
        Optional<FileEntity> fileDataOpt = fileEntityService.getById(fileId.getAsLong());
        if (fileDataOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No such file.");
            return;
        }
        FileEntity fileEntity = fileDataOpt.get();

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileEntity.getName() + "\"");
        resp.setCharacterEncoding("UTF-8");
        File file = new File(UPLOAD_PATH + fileDataOpt.get().getFilePath());
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
        ObjectMapper objectMapper = new ObjectMapper();
        FileTo fileTo = objectMapper.readValue(req.getReader(), FileTo.class);

        Optional<FileEntity> updated = fileEntityService.update(fileTo);

        if (updated.isPresent()) {
            ServletUtil.writeJsonResponse(resp, updated.get());
            resp.setStatus(HttpServletResponse.SC_CREATED);
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

        DiskFileItemFactory factory = new DiskFileItemFactory.Builder()
                .setBufferSize(MEM_MAX_SIZE)
                .setPath(Path.of(UPLOAD_PATH)).get();

        JakartaServletDiskFileUpload upload = new JakartaServletDiskFileUpload(factory);
        upload.setFileSizeMax(FILE_MAX_SIZE);

        try {
            List<DiskFileItem> formItems = upload.parseRequest(req);
            OptionalLong userId = OptionalLong.empty();
            if (formItems != null && formItems.size() > 0) {
                for (DiskFileItem item : formItems) {
                    if (item.isFormField() && item.getFieldName().equals("userId")) {
                        userId = OptionalLong.of(Long.parseLong(item.getString()));
                        break;
                    }
                }
                if (userId.isPresent()) {
                    for (DiskFileItem item : formItems) {
                        if (!item.isFormField() && item.getFieldName().equals("file")) {
                            String fileName = new File(item.getName()).getName();
                            String filePath = File.separator + fileName;
                            File storeFile = new File(UPLOAD_PATH + filePath);
                            Optional<FileEntity> savedFileData = fileEntityService.save(new FileEntity(fileName, filePath, FileStatus.AVAILABLE), userId.getAsLong());
                            if (savedFileData.isPresent()) {
                                item.write(storeFile.toPath());
                                resp.setStatus(HttpServletResponse.SC_CREATED);
                                ServletUtil.writeJsonResponse(resp, savedFileData.get());
                            } else {
                                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error saving file.");
                            }
                        }
                    }
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found.");
                }

            }
        } catch (Exception ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error uploading file: " + ex.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OptionalLong fileId = ServletUtil.parseLongParam(req, resp);
        if (fileId.isEmpty()) return;
        fileEntityService.deleteById(fileId.getAsLong());
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}