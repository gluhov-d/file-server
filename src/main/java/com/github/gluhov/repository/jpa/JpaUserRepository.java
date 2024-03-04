package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.Event;
import com.github.gluhov.model.User;
import com.github.gluhov.repository.UserRepository;
import com.github.gluhov.util.DatabaseUtil;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.Subgraph;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JpaUserRepository implements UserRepository {
    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();
   
    @Override
    public Optional<User> getById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (!transaction.isActive()) session.beginTransaction();
        EntityGraph<User> graph = session.createEntityGraph(User.class);
        graph.addAttributeNodes("events");
        Subgraph<Event> subgraph = graph.addSubgraph("events");
        subgraph.addAttributeNodes("file");
        Map<String, Object> options = new HashMap<>();
        options.put("jakarta.persistence.loadgraph", graph);
        return Optional.ofNullable(session.find(User.class, id, options));
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
}