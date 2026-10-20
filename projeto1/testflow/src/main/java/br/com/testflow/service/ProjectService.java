package br.com.testflow.service;

import br.com.testflow.models.Project;

import java.util.Optional;

public interface ProjectService {
    void create(Project project);

    Iterable<Project> findAll();

    Project findById(Long id);

    void update(Long id, Project project);

    void delete(Long id);
}
