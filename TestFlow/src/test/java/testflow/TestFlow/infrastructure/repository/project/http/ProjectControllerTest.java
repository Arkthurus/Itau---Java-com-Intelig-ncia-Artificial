package testflow.TestFlow.infrastructure.repository.project.http;

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
    void deveCriarProjetoEDocumentarRequisicaoEResposta() throws Exception {
        mockMvc.perform(post("/projects")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"projectName":"Projeto documentado"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.projectName").value("Projeto documentado"))
                .andDo(document("projects/create",
                        requestFields(fieldWithPath("projectName").description("Nome do projeto")),
                        responseFields(
                                fieldWithPath("id").description("Identificador único do projeto"),
                                fieldWithPath("projectName").description("Nome do projeto"))));
    }

    @Test
    void deveListarProjetosEDocumentarResposta() throws Exception {
        criarProjeto("Projeto listado");

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].projectName", hasItem("Projeto listado")))
                .andDo(document("projects/list",
                        responseFields(
                                fieldWithPath("[].id").description("Identificador único do projeto"),
                                fieldWithPath("[].projectName").description("Nome do projeto"))));
    }

    @Test
    void deveBuscarProjetoPorIdEDocumentarResposta() throws Exception {
        String idProjeto = criarProjeto("Detalhes do projeto");

        mockMvc.perform(get("/projects/{id}", idProjeto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idProjeto))
                .andExpect(jsonPath("$.projectName").value("Detalhes do projeto"))
                .andDo(document("projects/get-by-id",
                        pathParameters(parameterWithName("id").description("Identificador único do projeto")),
                        responseFields(
                                fieldWithPath("id").description("Identificador único do projeto"),
                                fieldWithPath("projectName").description("Nome do projeto"))));
    }

    @Test
    void deveAtualizarProjetoEDocumentarRequisicaoEResposta() throws Exception {
        String idProjeto = criarProjeto("Projeto original");

        mockMvc.perform(put("/projects/{id}", idProjeto)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {"newName":"Projeto atualizado"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idProjeto))
                .andExpect(jsonPath("$.projectName").value("Projeto atualizado"))
                .andDo(document("projects/update",
                        pathParameters(parameterWithName("id").description("Identificador único do projeto")),
                        requestFields(fieldWithPath("newName").description("Novo nome do projeto")),
                        responseFields(
                                fieldWithPath("id").description("Identificador único do projeto"),
                                fieldWithPath("projectName").description("Nome atualizado do projeto"))));
    }

    @Test
    void deveExcluirProjetoEDocumentarResposta() throws Exception {
        String idProjeto = criarProjeto("Projeto a excluir");

        mockMvc.perform(delete("/projects/{id}", idProjeto))
                .andExpect(status().isNoContent())
                .andDo(document("projects/delete",
                        pathParameters(parameterWithName("id").description("Identificador único do projeto"))));
    }

    private String criarProjeto(String nome) throws Exception {
        MvcResult result = mockMvc.perform(post("/projects")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(java.util.Map.of("projectName", nome))))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();
    }
}