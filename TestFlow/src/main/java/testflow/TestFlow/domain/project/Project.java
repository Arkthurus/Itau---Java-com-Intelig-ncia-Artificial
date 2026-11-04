package testflow.TestFlow.domain.project;

import java.lang.String;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

@Getter
@Setter
public class Project {

    private ProjectId id;
    private String name;

    //Construtor de criação
    public Project(String name) {
        this.id =  new ProjectId();
        this.name = name;
    }

    //Construtor de consulta ao banco
    public Project(ProjectId projectId, String name){

        Assert.notNull(projectId, "Id must not be null");
        Assert.notNull(name, "Project Name must not be null");

        this.id = projectId;
        this.name = name;

    }
}
