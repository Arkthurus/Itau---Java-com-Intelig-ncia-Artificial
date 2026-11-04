package testflow.TestFlow.domain.folder;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import testflow.TestFlow.domain.project.ProjectId;



@Getter
@Setter
public class Folder {
    private String name;
    private FolderId id;
    private ProjectId projectId;
    @Nullable
    private FolderId parentFolderId;

    //Construtor de criação
    public Folder(String name, ProjectId projectId, @Nullable FolderId parentFolderId) {
        Assert.hasText(name, "Nome da pasta não pode ser vazio");
        Assert.notNull(projectId, "ProjectId não pode ser nulo");

        this.id = new FolderId();
        this.name = name;
        this.projectId = projectId;
        this.parentFolderId = parentFolderId;
    }

    //Construtor de consulta ao banco
    public Folder(FolderId folderId, String name, ProjectId projectId, @Nullable FolderId parentFolderId) {

        Assert.notNull(folderId, "Id da pasta não pode ser nulo");
        Assert.notNull(name, "Nome da Pasta não pode ser vazio");
        Assert.notNull(projectId, "projectId não poder ser nulo.");

        this.id = folderId;
        this.name = name;
        this.projectId = projectId;
        this.parentFolderId = parentFolderId;

    }
}
