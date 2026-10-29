package testflow.TestFlow.domain.project;

import java.lang.String;
import java.util.UUID;

import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

@Getter
@Setter
public class Project {

    private ProjectId id;
    private String name;

    public Project(ProjectId projectId, String name){

        Assert.notNull(id, "Id must not be null");
        Assert.notNull(name, "Project Name must not be null");

        this.id = new ProjectId();
        this.name = name;

    }

    public void update(String name){
        this.name = name;
    }

}
