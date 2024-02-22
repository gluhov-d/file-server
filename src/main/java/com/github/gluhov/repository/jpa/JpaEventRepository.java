package com.github.gluhov.repository.jpa;

import com.github.gluhov.model.Event;
import com.github.gluhov.repository.EventRepository;
import com.github.gluhov.util.DatabaseUtil;
import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class JpaEventRepository implements EventRepository {

    private final String CHECK_EXISTS = "SELECT COUNT(id) FROM Event WHERE id = :id";

    private final String FIND_ALL = "SELECT e FROM Event e";

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
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(event);
            transaction.commit();
            return Optional.of(event);
        }
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

    @Override
    public Optional<List<Event>> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery(FIND_ALL, Event.class).getResultList());
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