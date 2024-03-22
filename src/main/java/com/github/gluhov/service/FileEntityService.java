package com.github.gluhov.service;

import com.github.gluhov.model.Event;
import com.github.gluhov.model.FileEntity;
import com.github.gluhov.repository.EventRepository;
import com.github.gluhov.repository.FileEntityRepository;
import com.github.gluhov.repository.UserRepository;
import com.github.gluhov.to.FileTo;
import com.github.gluhov.util.DatabaseUtil;
import com.github.gluhov.util.FileUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;

import java.util.Optional;

@RequiredArgsConstructor
public class FileEntityService {
    private final FileEntityRepository fileEntityRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Optional<FileEntity> getById(Long id) { return fileEntityRepository.getById(id); }

    public void deleteById(Long id) { fileEntityRepository.deleteById(id); }

    @Transactional
    public Optional<FileEntity> save(FileEntity fileEntity, Long userId) {
        return fileEntityRepository.save(fileEntity)
                .flatMap(file -> userRepository.getById(userId)
                        .map(user -> {
                            eventRepository.save(new Event(user, file));
                            return file;
                        }));
    }

    public Optional<FileEntity> update(FileTo fileTo) {
        return fileEntityRepository.getById(fileTo.getId())
                .flatMap(fileEntity -> fileEntityRepository.update(FileUtil.updateFromTo(fileEntity, fileTo)));
    }
}