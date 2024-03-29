package com.github.gluhov.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class User extends BaseEntity {
    @Column(name = "name")
    @NotBlank
    @Size(min = 2, max = 128)
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<Event> events = new HashSet<>();

    public User(Long id, String name) {
        super(id);
        this.name = name;
    }
}