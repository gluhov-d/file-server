package com.github.gluhov.service.file;

import com.github.gluhov.model.FileEntity;
import com.github.gluhov.model.FileStatus;
import com.github.gluhov.to.FileTo;

import java.util.Optional;

public class FileTestData {
    public static final long FILE_ID = 1;
    public static final long FILE_NOT_FOUND_ID = 100;

    public static final FileEntity file1 = new FileEntity(FILE_ID, "1.txt", "/1.txt", FileStatus.AVAILABLE);
    public static final FileEntity file3 = new FileEntity(FILE_ID + 2, "3.txt", "/3.txt", FileStatus.DELETED);

    public static Optional<FileEntity> getUpdated() { return Optional.of(new FileEntity(FILE_ID+2, "3.txt", "/3.txt", FileStatus.ARCHIVED));}
}