package testflow.TestFlow.infrastructure.repository.folder.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import testflow.TestFlow.application.folder.input.CreateFolderInput;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.application.folder.output.FolderTreeOutput;
import testflow.TestFlow.application.folder.usecases.CreateFolderUseCase;
import testflow.TestFlow.application.folder.usecases.DeleteFolderUseCase;
import testflow.TestFlow.application.folder.usecases.FindFolderTreeByProjectIdUseCase;
import testflow.TestFlow.application.folder.usecases.FindRootFoldersByProjectIdUseCase;
import testflow.TestFlow.application.folder.usecases.FindSubFoldersUseCase;
import testflow.TestFlow.application.folder.usecases.GetFolderByIdUseCase;
import testflow.TestFlow.application.folder.usecases.MoveFolderUseCase;
import testflow.TestFlow.application.folder.usecases.UpdateFolderUseCase;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.infrastructure.http.GlobalExceptionHandler;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
class FolderControllerTest {
    private MockMvc mockMvc;
    private CreateFolderUseCase createFolderUseCase;
    private GetFolderByIdUseCase getFolderByIdUseCase;
    private UpdateFolderUseCase updateFolderUseCase;
    private DeleteFolderUseCase deleteFolderUseCase;
    private MoveFolderUseCase moveFolderUseCase;
    private FindRootFoldersByProjectIdUseCase findRootFoldersByProjectIdUseCase;
    private FindSubFoldersUseCase findSubFoldersUseCase;
    private FindFolderTreeByProjectIdUseCase findFolderTreeByProjectIdUseCase;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        createFolderUseCase = mock(CreateFolderUseCase.class);
        getFolderByIdUseCase = mock(GetFolderByIdUseCase.class);
        updateFolderUseCase = mock(UpdateFolderUseCase.class);
        deleteFolderUseCase = mock(DeleteFolderUseCase.class);
        moveFolderUseCase = mock(MoveFolderUseCase.class);
        findRootFoldersByProjectIdUseCase = mock(FindRootFoldersByProjectIdUseCase.class);
        findSubFoldersUseCase = mock(FindSubFoldersUseCase.class);
        findFolderTreeByProjectIdUseCase = mock(FindFolderTreeByProjectIdUseCase.class);

        var controller = new FolderController(
                createFolderUseCase,
                getFolderByIdUseCase,
                updateFolderUseCase,
                deleteFolderUseCase,
                moveFolderUseCase,
                findRootFoldersByProjectIdUseCase,
                findSubFoldersUseCase,
                findFolderTreeByProjectIdUseCase
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void createsFolder() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID folderId = UUID.randomUUID();
        when(createFolderUseCase.execute(any(CreateFolderInput.class)))
                .thenReturn(new FolderOutput(folderId.toString(), "Raiz", projectId.toString(), null));

        mockMvc.perform(post("/projects/{projectId}/folders", projectId)
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Raiz\",\"parentFolderId\":null}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(folderId.toString()))
                .andExpect(jsonPath("$.parentFolderId").value(nullValue()))
                .andDo(document("folders/create",
                        pathParameters(parameterWithName("projectId").description("ID do projeto dono da pasta")),
                        requestFields(
                                fieldWithPath("name").description("Nome da pasta"),
                                fieldWithPath("parentFolderId").description("ID da pasta pai; null cria uma pasta raiz")
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID da pasta criada"),
                                fieldWithPath("name").description("Nome da pasta"),
                                fieldWithPath("projectId").description("ID do projeto"),
                                fieldWithPath("parentFolderId").description("ID da pasta pai ou null para uma pasta raiz")
                        )));
    }

    @Test
    void listsRootFolders() throws Exception {
        UUID projectId = UUID.randomUUID();
        FolderOutput root = new FolderOutput(UUID.randomUUID().toString(), "Raiz", projectId.toString(), null);
        when(findRootFoldersByProjectIdUseCase.execute(new ProjectId(projectId))).thenReturn(List.of(root));

        mockMvc.perform(get("/projects/{projectId}/folders", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", hasItem("Raiz")))
                .andDo(document("folders/list-roots",
                        pathParameters(parameterWithName("projectId").description("ID do projeto")),
                        responseFields(
                                fieldWithPath("[].id").description("ID da pasta raiz"),
                                fieldWithPath("[].name").description("Nome da pasta"),
                                fieldWithPath("[].projectId").description("ID do projeto"),
                                fieldWithPath("[].parentFolderId").description("Sempre null para pastas raiz")
                        )));
    }

    @Test
    void returnsCompleteFolderTree() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID rootId = UUID.randomUUID();
        FolderTreeOutput root = new FolderTreeOutput(
                rootId.toString(), "Raiz", projectId.toString(), null,
                List.of(new FolderTreeOutput(UUID.randomUUID().toString(), "Filha", projectId.toString(),
                        rootId.toString(), List.of()))
        );
        when(findFolderTreeByProjectIdUseCase.execute(new ProjectId(projectId))).thenReturn(List.of(root));

        mockMvc.perform(get("/projects/{projectId}/folders/tree", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].children[0].name").value("Filha"))
                .andDo(document("folders/tree",
                        pathParameters(parameterWithName("projectId").description("ID do projeto")),
                        responseFields(
                                fieldWithPath("[].id").description("ID da pasta"),
                                fieldWithPath("[].name").description("Nome da pasta"),
                                fieldWithPath("[].projectId").description("ID do projeto"),
                                fieldWithPath("[].parentFolderId").description("ID da pasta pai ou null para uma raiz"),
                                subsectionWithPath("[].children").description("Lista recursiva de subpastas")
                        )));
    }

    @Test
    void listsDirectSubfolders() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();
        FolderOutput child = new FolderOutput(UUID.randomUUID().toString(), "Filha", projectId.toString(), parentId.toString());
        when(findSubFoldersUseCase.execute(new ProjectId(projectId), new FolderId(parentId)))
                .thenReturn(List.of(child));

        mockMvc.perform(get("/projects/{projectId}/folders/{parentFolderId}/subfolders", projectId, parentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Filha"))
                .andDo(document("folders/list-subfolders",
                        pathParameters(
                                parameterWithName("projectId").description("ID do projeto"),
                                parameterWithName("parentFolderId").description("ID da pasta pai")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("ID da subpasta"),
                                fieldWithPath("[].name").description("Nome da subpasta"),
                                fieldWithPath("[].projectId").description("ID do projeto"),
                                fieldWithPath("[].parentFolderId").description("ID da pasta pai")
                        )));
    }

    @Test
    void getsFolderById() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID folderId = UUID.randomUUID();
        when(getFolderByIdUseCase.execute(new ProjectId(projectId), new FolderId(folderId)))
                .thenReturn(new FolderOutput(folderId.toString(), "Pasta", projectId.toString(), null));

        mockMvc.perform(get("/projects/{projectId}/folders/{folderId}", projectId, folderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pasta"))
                .andDo(document("folders/get-by-id",
                        pathParameters(
                                parameterWithName("projectId").description("ID do projeto"),
                                parameterWithName("folderId").description("ID da pasta")
                        ),
                        responseFields(
                                fieldWithPath("id").description("ID da pasta"),
                                fieldWithPath("name").description("Nome da pasta"),
                                fieldWithPath("projectId").description("ID do projeto"),
                                fieldWithPath("parentFolderId").description("ID da pasta pai ou null")
                        )));
    }

    @Test
    void updatesFolder() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID folderId = UUID.randomUUID();
        when(updateFolderUseCase.execute(any(ProjectId.class), any(FolderId.class), any()))
                .thenReturn(new FolderOutput(folderId.toString(), "Atualizada", projectId.toString(), null));

        mockMvc.perform(put("/projects/{projectId}/folders/{folderId}", projectId, folderId)
                        .contentType(APPLICATION_JSON)
                        .content("{\"newName\":\"Atualizada\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Atualizada"))
                .andDo(document("folders/update",
                        pathParameters(
                                parameterWithName("projectId").description("ID do projeto"),
                                parameterWithName("folderId").description("ID da pasta")
                        ),
                        requestFields(fieldWithPath("newName").description("Novo nome da pasta")),
                        responseFields(
                                fieldWithPath("id").description("ID da pasta"),
                                fieldWithPath("name").description("Nome atualizado da pasta"),
                                fieldWithPath("projectId").description("ID do projeto"),
                                fieldWithPath("parentFolderId").description("ID da pasta pai ou null")
                        )));
    }

    @Test
    void deletesFolder() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID folderId = UUID.randomUUID();
        doNothing().when(deleteFolderUseCase).execute(new ProjectId(projectId), new FolderId(folderId));

        mockMvc.perform(delete("/projects/{projectId}/folders/{folderId}", projectId, folderId))
                .andExpect(status().isNoContent())
                .andDo(document("folders/delete",
                        pathParameters(
                                parameterWithName("projectId").description("ID do projeto"),
                                parameterWithName("folderId").description("ID da pasta")
                        )));
    }

    @Test
    void movesFolderToParentOrProjectRoot() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID folderId = UUID.randomUUID();

        mockMvc.perform(patch("/projects/{projectId}/folders/{folderId}/parent", projectId, folderId)
                        .contentType(APPLICATION_JSON)
                        .content("{\"parentFolderId\":null}"))
                .andExpect(status().isNoContent())
                .andDo(document("folders/move",
                        pathParameters(
                                parameterWithName("projectId").description("ID do projeto"),
                                parameterWithName("folderId").description("ID da pasta a mover")
                        ),
                        requestFields(fieldWithPath("parentFolderId")
                                .description("ID da nova pasta pai; null move a pasta para a raiz"))));
    }

    @Test
    void returnsNotFoundWhenProjectDoesNotExist() throws Exception {
        UUID projectId = UUID.randomUUID();
        when(findRootFoldersByProjectIdUseCase.execute(new ProjectId(projectId)))
                .thenThrow(new ProjectNotFoundException(new ProjectId(projectId)));

        mockMvc.perform(get("/projects/{projectId}/folders", projectId))
                .andExpect(status().isNotFound())
                .andDo(document("folders/project-not-found",
                        pathParameters(parameterWithName("projectId").description("ID do projeto"))));
    }
}
