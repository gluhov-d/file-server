package com.github.gluhov.service;

import com.github.gluhov.model.Event;
import com.github.gluhov.model.FileEntity;
import com.github.gluhov.model.User;
import com.github.gluhov.repository.EventRepository;
import com.github.gluhov.repository.FileEntityRepository;
import com.github.gluhov.repository.UserRepository;
import com.github.gluhov.to.FileTo;
import com.github.gluhov.util.DatabaseUtil;
import com.github.gluhov.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

@RequiredArgsConstructor
public class FileEntityService {
    private final FileEntityRepository fileEntityRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();

    public Optional<FileEntity> getById(Long id) { return fileEntityRepository.getById(id); }

    public void deleteById(Long id) { fileEntityRepository.deleteById(id); }

    public Optional<FileEntity> save(FileEntity fileEntity, Long userId) {

        try (Session session = sessionFactory.getCurrentSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Optional<FileEntity> savedFile = fileEntityRepository.save(fileEntity);
                if (savedFile.isEmpty()) return Optional.empty();
                Optional<User> userOpt = userRepository.getById(userId);
                if (userOpt.isEmpty()) return Optional.empty();
                eventRepository.save(new Event(userOpt.get(), savedFile.get()));
                transaction.commit();
                return savedFile;
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

    public Optional<FileEntity> update(FileTo fileTo) {
        Optional<FileEntity> fileEntity = fileEntityRepository.getById(fileTo.getId());
        if (fileEntity.isPresent()) return fileEntityRepository.update(FileUtil.updateFromTo(fileEntity.get(), fileTo));
        return Optional.empty();
    }
}