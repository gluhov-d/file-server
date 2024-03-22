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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import org.apache.commons.fileupload2.core.DiskFileItem;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletDiskFileUpload;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalLong;

import static com.github.gluhov.util.FileUtil.*;
import static com.github.gluhov.util.ServletUtil.getUserIdFromFormItems;

@jakarta.ws.rs.Path("/storage/api/v1/files/")
@Tag(name = "Files", description = "Operations with files")
@WebServlet("/api/v1/files/*")
public class FileRestControllerV1 extends HttpServlet {
    private FileEntityService fileEntityService;

    @Override
    public void init() throws ServletException {
        super.init();
        fileEntityService = new FileEntityService(new JpaFileEntityRepository(), new JpaEventRepository(), new JpaUserRepository());
        initializeUploadDir(getServletContext());
    }

    @GET
    @jakarta.ws.rs.Path("/{fileId}")
    @Produces("application/octet-stream")
    @Operation(
            summary = "Download file by ID",
            description = "Downloads a file by its ID",
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "fileId",
                            required = true,
                            schema = @Schema(type = "long")
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
                            responseCode = "200",
                            description = "File downloaded successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "File not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error getting file data"
                    )
            }
    )
    @Override
    public void doGet(@Parameter(hidden = true) HttpServletRequest req, @Parameter(hidden = true) HttpServletResponse resp) throws ServletException, IOException {
        OptionalLong fileId = ServletUtil.parseLongParam(req, resp);
        if (fileId.isEmpty()) return;
        Optional<FileEntity> fileDataOpt = fileEntityService.getById(fileId.getAsLong());
        if (fileDataOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No such file.");
            return;
        }
        writeFileResponse(fileDataOpt.get(), resp);
    }

    @PUT
    @Produces("application/json")
    @Operation(
            summary = "Update file",
            description = "Updates file details with the provided information",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FileTo.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "File updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FileEntity.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error saving file data."
                    )
            }
    )
    @Override
    public void doPut(@Parameter(hidden = true) HttpServletRequest req, @Parameter(hidden = true) HttpServletResponse resp) throws ServletException, IOException {
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

    @POST
    @Consumes("multipart/form-data")
    @Produces("application/json")
    @Operation(
            summary = "Upload file",
            description = "Uploads a file with associated user ID",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(
                                    type = "object"
                            ),
                            schemaProperties = {
                                    @SchemaProperty(
                                            name = "userId",
                                            schema = @Schema(implementation = Long.class)
                                    ),
                                     @SchemaProperty(
                                             name = "file",
                                             schema = @Schema(implementation = File.class)
                                     )
                            }

                    )
            ),
            parameters = {
                @Parameter(
                        required = true,
                        name = "userId",
                        content = @Content(
                                mediaType = "multipart/form-data",
                                schema = @Schema(implementation = Long.class)
                        )
                ),
                    @Parameter(
                            required = true,
                            name = "file",
                            content = @Content(
                                    mediaType = "multipart/form-data",
                                    schema = @Schema(implementation = File.class)
                            )
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "File uploaded successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FileEntity.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - Form must have enctype=multipart/form-data."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error uploading file."
                    )
            }
    )
    @Override
    public void doPost(@Parameter(hidden = true) HttpServletRequest req, @Parameter(hidden = true) HttpServletResponse resp) throws ServletException, IOException {
        if (!req.getContentType().toLowerCase(Locale.ENGLISH).startsWith("multipart/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Form must have enctype=multipart/form-data.");
            return;
        }

        DiskFileItemFactory factory = createDiskFileItemFactory();
        JakartaServletDiskFileUpload upload = createDiskFileUpload(factory);

        try {
            List<DiskFileItem> formItems = upload.parseRequest(req);
            OptionalLong userId = getUserIdFromFormItems(formItems);
            if (userId.isPresent()) {
                processFileItem(formItems, userId.getAsLong(), resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found.");
            }
        } catch (Exception ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error uploading file: " + ex.getMessage());
        }
    }

    private void processFileItem(List<DiskFileItem> formItems, long userId, HttpServletResponse resp) throws IOException {
        for (DiskFileItem item : formItems) {
            if (!item.isFormField() && item.getFieldName().equals("file")) {
                String fileName = new File(item.getName()).getName();
                String filePath = File.separator + fileName;
                File storeFile = new File(UPLOAD_PATH + filePath);
                Optional<FileEntity> savedFileData = fileEntityService.save(new FileEntity(fileName, filePath, FileStatus.AVAILABLE), userId);
                if (savedFileData.isPresent()) {
                    item.write(storeFile.toPath());
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    ServletUtil.writeJsonResponse(resp, savedFileData.get());
                } else {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error saving file.");
                }
            }
        }
    }

    @DELETE
    @jakarta.ws.rs.Path("/{fileId}")
    @Produces("application/json")
    @Operation(
            summary = "Delete file by ID",
            description = "Deletes a file by its ID",
            parameters = {
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "fileId",
                            required = true,
                            schema = @Schema(type = "long")
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
                            responseCode = "204",
                            description = "File deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "File not found"
                    )
            }
    )
    @Override
    public void doDelete(@Parameter(hidden = true) HttpServletRequest req, @Parameter(hidden = true) HttpServletResponse resp) throws ServletException, IOException {
        OptionalLong fileId = ServletUtil.parseLongParam(req, resp);
        if (fileId.isEmpty()) return;
        fileEntityService.deleteById(fileId.getAsLong());
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}