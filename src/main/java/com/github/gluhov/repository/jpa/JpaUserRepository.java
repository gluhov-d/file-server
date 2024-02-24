package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.User;
import com.github.gluhov.repository.UserRepository;
import com.github.gluhov.util.DatabaseUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class JpaUserRepository implements UserRepository {
    private final String GET_BY_ID = "SELECT u FROM User u \n" +
            "LEFT JOIN FETCH u.events e\n" +
            "LEFT JOIN FETCH e.file f\n" +
            "WHERE u.id = :id";
    private final String CHECK_EXISTS = "SELECT COUNT(id) FROM User WHERE id = :id";
    private final String FIND_ALL = "SELECT u FROM User u";
    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();
   
    @Override
    public Optional<User> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery(GET_BY_ID, User.class).setParameter("id", id).getSingleResult());
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, id);
                if (user != null) {
                    session.remove(user);
                    transaction.commit();
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                System.out.println("Failed to delete user.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public Optional<User> save(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return Optional.of(user);
        }
    }

    @Override
    public Optional<User> update(User user) {
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            User updatedUser = session.merge(user);
            transaction.commit();
            return Optional.of(updatedUser);
        }
    }

    @Override
    public Optional<List<User>> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery(FIND_ALL, User.class).getResultList());
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
}