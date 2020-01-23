package com.itis.javalab.dao.interfaces;

import com.itis.javalab.context.annotations.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface CrudDao<T> {
    Optional<T> find(Long id);
    void save(T model);
    void update(T model);
    void delete(Long id);

    List<T> findAll();
}