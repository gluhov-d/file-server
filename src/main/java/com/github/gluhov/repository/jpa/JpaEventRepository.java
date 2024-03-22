package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.Event;
import com.github.gluhov.repository.EventRepository;
import com.github.gluhov.util.DatabaseUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

public class JpaEventRepository implements EventRepository {
    private final SessionFactory sessionFactory = DatabaseUtil.getSessionFactory();

    @Override
    public Optional<Event> getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Event event = session.get(Event.class, id);
            return Optional.ofNullable(event);
        }
    }

    @Override
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Event event = session.get(Event.class, id);
                if (event != null) {
                    session.remove(event);
                    transaction.commit();
                }
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                System.out.println("Failed to delete file upload event.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public Optional<Event> save(Event event) {
        throw new RuntimeException("Saving event exception");
    }

    @Override
    public Optional<Event> update(Event event) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            Event updatedEvent = session.merge(event);
            transaction.commit();
            return Optional.of(updatedEvent);
        }
    }
}