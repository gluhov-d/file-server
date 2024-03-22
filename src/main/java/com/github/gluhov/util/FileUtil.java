package com.github.gluhov.util;

import com.github.gluhov.model.FileEntity;
import com.github.gluhov.to.FileTo;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@UtilityClass
public class FileUtil {
    private final int FILE_MAX_SIZE = 100 * 1024;
    private final int MEM_MAX_SIZE = 100 * 1024;
    public String UPLOAD_PATH;

    public static void initializeUploadDir(ServletContext context) throws ServletException{

        String relativePath = "resources/uploads";
        UPLOAD_PATH = context.getRealPath(relativePath);

        File uploadDir = new File(UPLOAD_PATH);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new ServletException("Can not get path to upload directory: " + UPLOAD_PATH);
        }

    }

    public static FileEntity updateFromTo(FileEntity fileEntity, FileTo fileTo) {
        fileEntity.setFileStatus(fileTo.getFileStatus());
        return fileEntity;
    }

    public static FileTo createTo(FileEntity fileEntity) {
        return new FileTo(fileEntity.getId(), fileEntity.getName(), fileEntity.getFilePath(), fileEntity.getFileStatus());
    }

    public static DiskFileItemFactory createDiskFileItemFactory() {
        return new DiskFileItemFactory.Builder()
                .setBufferSize(MEM_MAX_SIZE)
                .setPath(Path.of(UPLOAD_PATH)).get();
    }

    public static JakartaServletDiskFileUpload createDiskFileUpload(DiskFileItemFactory factory) {
        JakartaServletDiskFileUpload upload = new JakartaServletDiskFileUpload(factory);
        upload.setFileSizeMax(FILE_MAX_SIZE);
        return upload;
    }

    public static void writeFileResponse(FileEntity fileEntity, HttpServletResponse resp) throws ServletException {
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileEntity.getName() + "\"");
        resp.setCharacterEncoding("UTF-8");
        File file = new File(UPLOAD_PATH + fileEntity.getFilePath());
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
}