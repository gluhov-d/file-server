package com.github.gluhov;

import com.github.gluhov.util.DatabaseUtil;

public class FileServerApplication {
    public static void main(String[] args) {
        DatabaseUtil.migrateDatabase();
        System.out.println("Migration complete!");
    }
}