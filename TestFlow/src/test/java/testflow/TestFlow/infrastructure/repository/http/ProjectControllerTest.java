package testflow.TestFlow.infrastructure.repository.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class,  SpringExtension.class})
@SpringBootTest
class ProjectControllerTest {

    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void shouldCreateProjectAndDocumentRequestAndResponse() throws Exception {
        mockMvc.perform(post("/projects")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"projectName":"Documented project"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.projectName").value("Documented project"))
                .andDo(document("projects/create",
                        requestFields(fieldWithPath("projectName").description("Name of the project")),
                        responseFields(
                                fieldWithPath("id").description("Unique identifier of the project"),
                                fieldWithPath("projectName").description("Name of the project"))));
    }

    @Test
    void shouldListProjectsAndDocumentResponse() throws Exception {
        createProject("Listed project");

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].projectName", hasItem("Listed project")))
                .andDo(document("projects/list",
                        responseFields(
                                fieldWithPath("[].id").description("Unique identifier of the project"),
                                fieldWithPath("[].projectName").description("Name of the project"))));
    }

    @Test
    void shouldGetProjectByIdAndDocumentResponse() throws Exception {
        String projectId = createProject("Project details");

        mockMvc.perform(get("/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.projectName").value("Project details"))
                .andDo(document("projects/get-by-id",
                        pathParameters(parameterWithName("id").description("Unique identifier of the project")),
                        responseFields(
                                fieldWithPath("id").description("Unique identifier of the project"),
                                fieldWithPath("projectName").description("Name of the project"))));
    }

    @Test
    void shouldUpdateProjectAndDocumentRequestAndResponse() throws Exception {
        String projectId = createProject("Original project");

        mockMvc.perform(put("/projects/{id}", projectId)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"newName":"Updated project"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.projectName").value("Updated project"))
                .andDo(document("projects/update",
                        pathParameters(parameterWithName("id").description("Unique identifier of the project")),
                        requestFields(fieldWithPath("newName").description("New name of the project")),
                        responseFields(
                                fieldWithPath("id").description("Unique identifier of the project"),
                                fieldWithPath("projectName").description("Updated name of the project"))));
    }

    @Test
    void shouldDeleteProjectAndDocumentResponse() throws Exception {
        String projectId = createProject("Project to delete");

        mockMvc.perform(delete("/projects/{id}", projectId))
                .andExpect(status().isNoContent())
                .andDo(document("projects/delete",
                        pathParameters(parameterWithName("id").description("Unique identifier of the project"))));
    }

    private String createProject(String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/projects")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of("projectName", name))))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }
}