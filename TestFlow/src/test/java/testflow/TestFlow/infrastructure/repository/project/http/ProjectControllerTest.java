package testflow.TestFlow.infrastructure.repository.project.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import testflow.TestFlow.application.project.output.ProjectOutput;
import testflow.TestFlow.application.project.usecases.CreateProjectUseCase;
import testflow.TestFlow.application.project.usecases.DeleteProjectUseCase;
import testflow.TestFlow.application.project.usecases.GetAllProjectsUseCase;
import testflow.TestFlow.application.project.usecases.GetProjectByIdUseCase;
import testflow.TestFlow.application.project.usecases.UpdateProjectUseCase;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.infrastructure.http.GlobalExceptionHandler;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
class ProjectControllerTest {
    private MockMvc mockMvc;
    private CreateProjectUseCase createProjectUseCase;
    private GetAllProjectsUseCase getAllProjectsUseCase;
    private GetProjectByIdUseCase getProjectByIdUseCase;
    private UpdateProjectUseCase updateProjectUseCase;
    private DeleteProjectUseCase deleteProjectUseCase;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        createProjectUseCase = mock(CreateProjectUseCase.class);
        getAllProjectsUseCase = mock(GetAllProjectsUseCase.class);
        getProjectByIdUseCase = mock(GetProjectByIdUseCase.class);
        updateProjectUseCase = mock(UpdateProjectUseCase.class);
        deleteProjectUseCase = mock(DeleteProjectUseCase.class);

        var controller = new ProjectController(
                createProjectUseCase,
                getAllProjectsUseCase,
                getProjectByIdUseCase,
                updateProjectUseCase,
                deleteProjectUseCase
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void createsProjectAndDocumentsRequestAndResponse() throws Exception {
        UUID id = UUID.randomUUID();
        when(createProjectUseCase.execute(any())).thenReturn(new ProjectOutput(id.toString(), "Projeto"));

        mockMvc.perform(post("/projects")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\":\"Projeto\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.projectName").value("Projeto"))
                .andDo(document("projects-v2/create",
                        requestFields(fieldWithPath("name").description("Nome do projeto")),
                        responseFields(
                                fieldWithPath("id").description("ID único do projeto"),
                                fieldWithPath("projectName").description("Nome do projeto")
                        )));
    }

    @Test
    void listsProjectsAndDocumentsResponse() throws Exception {
        when(getAllProjectsUseCase.execute()).thenReturn(List.of(
                new ProjectOutput(UUID.randomUUID().toString(), "Projeto A"),
                new ProjectOutput(UUID.randomUUID().toString(), "Projeto B")
        ));

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].projectName", hasItem("Projeto A")))
                .andDo(document("projects-v2/list",
                        responseFields(
                                fieldWithPath("[].id").description("ID único do projeto"),
                                fieldWithPath("[].projectName").description("Nome do projeto")
                        )));
    }

    @Test
    void getsProjectByIdAndDocumentsResponse() throws Exception {
        UUID id = UUID.randomUUID();
        when(getProjectByIdUseCase.execute(new ProjectId(id)))
                .thenReturn(new ProjectOutput(id.toString(), "Projeto"));

        mockMvc.perform(get("/projects/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andDo(document("projects-v2/get-by-id",
                        pathParameters(parameterWithName("id").description("ID do projeto")),
                        responseFields(
                                fieldWithPath("id").description("ID único do projeto"),
                                fieldWithPath("projectName").description("Nome do projeto")
                        )));
    }

    @Test
    void updatesProjectAndDocumentsRequestAndResponse() throws Exception {
        UUID id = UUID.randomUUID();
        when(updateProjectUseCase.execute(any(ProjectId.class), any()))
                .thenReturn(new ProjectOutput(id.toString(), "Projeto atualizado"));

        mockMvc.perform(put("/projects/{id}", id)
                        .contentType(APPLICATION_JSON)
                        .content("{\"newName\":\"Projeto atualizado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Projeto atualizado"))
                .andDo(document("projects-v2/update",
                        pathParameters(parameterWithName("id").description("ID do projeto")),
                        requestFields(fieldWithPath("newName").description("Novo nome do projeto")),
                        responseFields(
                                fieldWithPath("id").description("ID único do projeto"),
                                fieldWithPath("projectName").description("Nome atualizado do projeto")
                        )));
    }

    @Test
    void deletesProjectAndDocumentsResponse() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(deleteProjectUseCase).execute(new ProjectId(id));

        mockMvc.perform(delete("/projects/{id}", id))
                .andExpect(status().isNoContent())
                .andDo(document("projects-v2/delete",
                        pathParameters(parameterWithName("id").description("ID do projeto"))));
    }

    @Test
    void returnsNotFoundWhenProjectDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(getProjectByIdUseCase.execute(new ProjectId(id)))
                .thenThrow(new ProjectNotFoundException(new ProjectId(id)));

        mockMvc.perform(get("/projects/{id}", id))
                .andExpect(status().isNotFound())
                .andDo(document("projects-v2/not-found",
                        pathParameters(parameterWithName("id").description("ID do projeto"))));
    }
}
