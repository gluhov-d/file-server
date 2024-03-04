package com.github.gluhov.util;

import com.github.gluhov.model.Event;
import com.github.gluhov.to.EventTo;
import lombok.experimental.UtilityClass;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class EventUtil {

    public static EventTo createTo(Event event) {
        return new EventTo(event.getId(), event.getUser().getId(), FileUtil.createTo(event.getFile()));
    }

    public static Set<EventTo> createTos(Set<Event> events) {
        return events.stream()
                .map(event -> new EventTo(event.getId(), event.getUser().getId(), FileUtil.createTo(event.getFile())))
                .collect(Collectors.toSet());
    }
}