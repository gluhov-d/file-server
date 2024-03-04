package com.github.gluhov.repository;

import java.util.Optional;

public interface GenericRepository <T, ID> {

    Optional<T> getById(ID id);

    void deleteById(ID id);

    Optional<T> save(T entity);

    Optional<T> update(T entity);
}