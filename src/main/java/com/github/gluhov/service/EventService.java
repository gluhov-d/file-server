package com.github.gluhov.service;

import com.github.gluhov.model.Event;
import com.github.gluhov.repository.EventRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Optional<Event> getById(Long id) { return eventRepository.getById(id); }
    public void deleteById(Long id) { eventRepository.deleteById(id); }
    public Optional<Event> save(Event event) { return eventRepository.save(event); }
    public Optional<Event> update(Event event) { return eventRepository.update(event); }
    public Optional<List<Event>> findAll() { return eventRepository.findAll(); }
    public Boolean checkIfExist(Long id) { return eventRepository.checkIfExist(id); }
}
