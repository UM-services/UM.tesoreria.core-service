package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.mapper.UsuarioDtoMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(UsuarioController.class)
@Import(UsuarioDtoMapper.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private UsuarioService service;

    private static final String CONFIG_JSON = """
            {"nombre":"Pedro","geograficaId":1,"imprimeChequera":1,"numeroOpManual":0,
             "habilitaOpEliminacion":0,"eliminaChequera":0,"modificaChequera":0,
             "activo":1,"administrador":0,"usuarioExterno":0}
            """;

    @Test
    void searchTodosPorTexto() {
        when(service.searchTodos("pedro"))
                .thenReturn(List.of(Usuario.builder().userId(7L).login("pedro").nombre("Pedro").build()));

        mockMvc.get().uri("/api/tesoreria/core/usuario/searchTodos/pedro")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].login").isEqualTo("pedro");
    }

    @Test
    void searchTodosPadronCompleto() {
        when(service.searchTodos(null))
                .thenReturn(List.of(Usuario.builder().userId(7L).login("pedro").build()));

        mockMvc.get().uri("/api/tesoreria/core/usuario/searchTodos")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].userId").isEqualTo(7);
    }

    @Test
    void findByIdOk() {
        when(service.findByUserId(7L)).thenReturn(Optional.of(Usuario.builder().userId(7L).nombre("Pedro").build()));

        mockMvc.get().uri("/api/tesoreria/core/usuario/usuario/id/7")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.nombre").isEqualTo("Pedro");
    }

    @Test
    void findByIdNotFound() {
        when(service.findByUserId(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/usuario/usuario/id/99")
                .assertThat().hasStatus(404);
    }

    @Test
    void updateConfiguracionOk() {
        when(service.updateConfiguracion(any(), eq(7L)))
                .thenReturn(Usuario.builder().userId(7L).login("pedro").nombre("Pedro").build());

        mockMvc.put().uri("/api/tesoreria/core/usuario/usuario/7/configuracion")
                .contentType(MediaType.APPLICATION_JSON).content(CONFIG_JSON)
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.nombre").isEqualTo("Pedro");
    }

    @Test
    void updateConfiguracionSinCamposObligatoriosDevuelve400() {
        mockMvc.put().uri("/api/tesoreria/core/usuario/usuario/7/configuracion")
                .contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Pedro\"}")
                .assertThat().hasStatus(400);
    }

    @Test
    void updateEstadoOk() {
        when(service.updateEstado(7L, (byte) 0))
                .thenReturn(Usuario.builder().userId(7L).activo((byte) 0).build());

        mockMvc.put().uri("/api/tesoreria/core/usuario/usuario/7/activo/0")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.activo").isEqualTo(0);
    }

    @Test
    void updateEstadoValorInvalidoDevuelve400() {
        mockMvc.put().uri("/api/tesoreria/core/usuario/usuario/7/activo/2")
                .assertThat().hasStatus(400);
    }

    @Test
    void resetPasswordOk() {
        when(service.resetPassword(7L, "nueva123"))
                .thenReturn(Usuario.builder().userId(7L).login("pedro").build());

        mockMvc.put().uri("/api/tesoreria/core/usuario/usuario/7/password")
                .contentType(MediaType.APPLICATION_JSON).content("{\"password\":\"nueva123\",\"reClave\":\"nueva123\"}")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.userId").isEqualTo(7);
    }

    @Test
    void resetPasswordClavesNoCoincidenDevuelve400() {
        mockMvc.put().uri("/api/tesoreria/core/usuario/usuario/7/password")
                .contentType(MediaType.APPLICATION_JSON).content("{\"password\":\"a\",\"reClave\":\"b\"}")
                .assertThat().hasStatus(400);
    }
}
