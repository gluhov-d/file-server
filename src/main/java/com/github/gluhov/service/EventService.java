package com.github.gluhov.service;

import com.github.gluhov.model.Event;
import com.github.gluhov.repository.EventRepository;
import com.github.gluhov.to.EventTo;
import com.github.gluhov.util.EventUtil;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public Optional<EventTo> getById(Long id) {
        Optional<Event> event = eventRepository.getById(id);
        return event.map(EventUtil::createTo);
    }
}
