package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.Event;
import com.github.gluhov.model.FileData;
import com.github.gluhov.model.FileStatus;
import com.github.gluhov.repository.FileDataRepository;
import com.github.gluhov.util.DatabaseUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class JpaFileDataDataRepository implements FileDataRepository {
    private final String CHECK_EXISTS = "SELECT COUNT(id) FROM FileData WHERE id = :id";
    private final String FIND_ALL = "SELECT f FROM FileData f";
    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();

    @Override
    public Optional<FileData> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            FileData fileData = session.get(FileData.class, id);
            return Optional.ofNullable(fileData);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                FileData fileData = session.get(FileData.class, id);
                if (fileData != null) {
                    fileData.setFileStatus(FileStatus.DELETED);
                    session.merge(fileData);
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
    public Optional<FileData> save(FileData fileData) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(fileData);
            transaction.commit();
            return Optional.of(fileData);
        }
    }

    @Override
    public Optional<FileData> update(FileData fileData) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            FileData updatedFileData = session.merge(fileData);
            transaction.commit();
            return Optional.of(updatedFileData);
        }
    }

    @Override
    public Optional<List<FileData>> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery(FIND_ALL, FileData.class).getResultList());
        }
    }

    @Override
    public Boolean checkIfExist(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery(CHECK_EXISTS, Long.class);
            query.setParameter("id", id);
            Long res = (Long) query.getSingleResult();
            return res == 1;
        }
    }

    @Override
    public Optional<FileData> saveWithEvent(FileData fileData, Event event) {
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.persist(fileData);
            session.persist(event);
            transaction.commit();
            return Optional.of(fileData);
        }
    }
}