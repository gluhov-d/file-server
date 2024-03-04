package com.github.gluhov.service.event;

import com.github.gluhov.repository.EventRepository;
import com.github.gluhov.service.EventService;
import com.github.gluhov.to.EventTo;
import com.github.gluhov.util.EventUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.github.gluhov.service.event.EventTestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    void getById() {
        when(eventRepository.getById(EVENT_ID)).thenReturn(Optional.of(event1));
        Optional<EventTo> result = eventService.getById(EVENT_ID);
        assertTrue(result.isPresent());
        assertEquals(EventUtil.createTo(event1), result.get());
    }

    @Test
    void getByIdNotFound() {
        when(eventRepository.getById(EVENT_NOT_FOUND_ID)).thenReturn(Optional.empty());
        Optional<EventTo> result = eventService.getById(EVENT_NOT_FOUND_ID);
        assertFalse(result.isPresent());
    }
}