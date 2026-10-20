package br.com.testflow.controller;

import br.com.testflow.models.Project;
import br.com.testflow.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectRestController {
    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<Project> create(@RequestBody Project project) {
        projectService.create(project);
        return ResponseEntity.status(201).body(project);
    }

    @GetMapping
    public ResponseEntity<Iterable<Project>> findAll() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody Project project) {
        projectService.update(id, project);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Project> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
