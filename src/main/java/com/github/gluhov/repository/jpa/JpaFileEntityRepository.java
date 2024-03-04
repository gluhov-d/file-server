package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.FileEntity;
import com.github.gluhov.model.FileStatus;
import com.github.gluhov.repository.FileEntityRepository;
import com.github.gluhov.util.DatabaseUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

public class JpaFileEntityRepository implements FileEntityRepository {
    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();

    @Override
    public Optional<FileEntity> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            FileEntity fileEntity = session.get(FileEntity.class, id);
            return Optional.ofNullable(fileEntity);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                FileEntity fileEntity = session.get(FileEntity.class, id);
                if (fileEntity != null) {
                    fileEntity.setFileStatus(FileStatus.DELETED);
                    session.merge(fileEntity);
                    transaction.commit();
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                System.out.println("Failed to delete file data.");
                e.printStackTrace();
            }
        }

    }

    @Override
    public Optional<FileEntity> save(FileEntity fileEntity) {
        Session session = sessionFactory.getCurrentSession();
        session.persist(fileEntity);
        return Optional.of(fileEntity);
    }

    @Override
    public Optional<FileEntity> update(FileEntity fileEntity) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            FileEntity updatedFileEntity = session.merge(fileEntity);
            transaction.commit();
            return Optional.of(updatedFileEntity);
        }
    }
}