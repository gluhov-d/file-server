package com.github.gluhov;

import com.github.gluhov.util.DatabaseUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.ApplicationPath;

@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "File storage application",
                description = "This is a simple Rest API application",
                version = "1.0.0",
                contact = @io.swagger.v3.oas.annotations.info.Contact(
                        name = "Dmitrii Glukhov",
                        url = "https://t.me/dmitriiGluhov",
                        email = "glukhov.d@gmail.com")
        ),

        servers = @Server(
                url = "http://localhost:8080"
        )
)
@ApplicationPath("/storage/api/v1/")
public class FileServerApplication {
    public static void main(String[] args) {
        DatabaseUtil.migrateDatabase();
        System.out.println("Migration complete!");
    }
}