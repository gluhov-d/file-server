package com.github.gluhov.to;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

@AllArgsConstructor
@Value
@EqualsAndHashCode(callSuper = true)
public class EventTo extends BaseTo {
    Long userId;

    FileTo fileTo;

    public EventTo(Long id, long userId, FileTo fileTo) {
        super(id);
        this.userId = userId;
        this.fileTo = fileTo;
    }
}