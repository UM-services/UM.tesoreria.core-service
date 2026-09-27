package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.web.dto.GeograficaResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.service.UsuarioChequeraGeograficaService;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.dto.UsuarioChequeraGeograficaResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.mapper.UsuarioChequeraGeograficaDtoMapper;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(UsuarioChequeraGeograficaController.class)
class UsuarioChequeraGeograficaControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private UsuarioChequeraGeograficaService service;

    @MockitoBean
    private UsuarioChequeraGeograficaDtoMapper mapper;

    @Test
    void findAllByUserId_returnsList() {
        var domain = UsuarioChequeraGeografica.builder().usuarioChequeraGeograficaId(1L).build();
        var response = UsuarioChequeraGeograficaResponse.builder().usuarioChequeraGeograficaId(1L).build();

        when(service.findAllByUserId(1L)).thenReturn(List.of(domain));
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/usuarioChequeraGeografica/user/1")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$").asArray().hasSize(1);
    }

    @Test
    void findAllByUserId_propagatesGeografica() {
        var domain = UsuarioChequeraGeografica.builder().usuarioChequeraGeograficaId(1L).geograficaId(20).build();
        var response = UsuarioChequeraGeograficaResponse.builder()
                .usuarioChequeraGeograficaId(1L)
                .geograficaId(20)
                .geografica(new GeograficaResponse(20, "Sede Norte", (byte) 0))
                .build();

        when(service.findAllByUserId(2L)).thenReturn(List.of(domain));
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/usuarioChequeraGeografica/user/2")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$[0].geografica.nombre").isEqualTo("Sede Norte");
    }

    @Test
    void findAllByUserId_whenEmpty_returnsEmptyList() {
        when(service.findAllByUserId(3L)).thenReturn(List.of());

        mockMvc.get().uri("/api/tesoreria/core/usuarioChequeraGeografica/user/3")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$").asArray().isEmpty();
    }

}
