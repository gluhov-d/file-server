package com.github.gluhov.service;

import com.github.gluhov.model.Event;
import com.github.gluhov.model.FileData;
import com.github.gluhov.model.User;
import com.github.gluhov.repository.FileDataRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class FileDataService {
    private final FileDataRepository fileDataRepository;

    public Optional<FileData> getById(Long id) { return fileDataRepository.getById(id); }
    public void deleteById(Long id) { fileDataRepository.deleteById(id); }
    public Optional<FileData> save(FileData fileData, User user) {
        return fileDataRepository.saveWithEvent(fileData, new Event(user, fileData));
    }
    public Optional<FileData> update(FileData fileData) { return fileDataRepository.update(fileData); }
    public Optional<List<FileData>> findAll() { return fileDataRepository.findAll(); }
    public Boolean checkIfExist(Long id) { return fileDataRepository.checkIfExist(id); }
}