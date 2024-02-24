package com.github.gluhov.repository;

import com.github.gluhov.model.Event;
import com.github.gluhov.model.FileData;

import java.util.Optional;

public interface FileDataRepository extends GenericRepository<FileData, Long>{
    Optional<FileData> saveWithEvent(FileData fileData, Event event);
}