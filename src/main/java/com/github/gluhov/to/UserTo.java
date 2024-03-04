package com.github.gluhov.to;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Set;

@Value
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class UserTo extends BaseTo {
    @NotBlank
    @Size(min = 2, max = 128)
    String name;

    Set<EventTo> eventTos;

    public UserTo(Long id, String name, Set<EventTo> eventTos) {
        super(id);
        this.name = name;
        this.eventTos = eventTos;
    }
}