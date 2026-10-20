package testflow.TestFlow.domain;

import java.lang.String;
import java.util.Optional;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableMBeanExport;
@Getter
@Setter
public class Project {

    private ProjectId id;
    private String name;

    public Project(String name){
        this.id = new ProjectId();
        this.name = name ;
    }

    public void update(Optional<String> name){
        name.ifPresent(this::setName);
    }
}
