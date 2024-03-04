package com.github.gluhov.util;

import com.github.gluhov.model.FileEntity;
import com.github.gluhov.to.FileTo;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUtil {

    public static FileEntity updateFromTo(FileEntity fileEntity, FileTo fileTo) {
        fileEntity.setFileStatus(fileTo.getFileStatus());
        return fileEntity;
    }

    public static FileTo createTo(FileEntity fileEntity) {
        return new FileTo(fileEntity.getId(), fileEntity.getName(), fileEntity.getFilePath(), fileEntity.getFileStatus());
    }
}