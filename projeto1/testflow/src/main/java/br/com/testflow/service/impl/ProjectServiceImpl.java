package br.com.testflow.service.impl;

import br.com.testflow.models.Project;
import br.com.testflow.repository.ProjectRepository;
import br.com.testflow.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public void create(Project project) {
        projectRepository.save(project);
    }

    @Override
    public Iterable<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project findById(Long id) {
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("Projeto não encontrado."));
    }

    @Override
    public void update(Long id, Project project) {
        Project projectDb = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado."));

        projectDb.setName(project.getName());

        projectRepository.save(projectDb);
    }

    @Override
    public void delete(Long id) {
        projectRepository.deleteById(id);
    }
}
