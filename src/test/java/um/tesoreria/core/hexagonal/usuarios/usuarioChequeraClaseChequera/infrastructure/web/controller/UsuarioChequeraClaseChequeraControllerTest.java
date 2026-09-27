package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.dto.ClaseChequeraResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.service.UsuarioChequeraClaseChequeraService;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.dto.UsuarioChequeraClaseChequeraResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.mapper.UsuarioChequeraClaseChequeraDtoMapper;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(UsuarioChequeraClaseChequeraController.class)
class UsuarioChequeraClaseChequeraControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private UsuarioChequeraClaseChequeraService service;

    @MockitoBean
    private UsuarioChequeraClaseChequeraDtoMapper mapper;

    @Test
    void findAllByUserId_returnsList() {
        var domain = UsuarioChequeraClaseChequera.builder().usuarioChequeraClaseChequeraId(1L).build();
        var response = UsuarioChequeraClaseChequeraResponse.builder().usuarioChequeraClaseChequeraId(1L).build();

        when(service.findAllByUserId(1L)).thenReturn(List.of(domain));
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/usuarioChequeraClaseChequera/user/1")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$").asArray().hasSize(1);
    }

    @Test
    void findAllByUserId_propagatesClaseChequera() {
        var domain = UsuarioChequeraClaseChequera.builder().usuarioChequeraClaseChequeraId(1L).claseChequeraId(20).build();
        var response = UsuarioChequeraClaseChequeraResponse.builder()
                .usuarioChequeraClaseChequeraId(1L)
                .claseChequeraId(20)
                .claseChequera(ClaseChequeraResponse.builder().claseChequeraId(20).nombre("Planilla").build())
                .build();

        when(service.findAllByUserId(2L)).thenReturn(List.of(domain));
        when(mapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/usuarioChequeraClaseChequera/user/2")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$[0].claseChequera.nombre").isEqualTo("Planilla");
    }

    @Test
    void findAllByUserId_whenEmpty_returnsEmptyList() {
        when(service.findAllByUserId(3L)).thenReturn(List.of());

        mockMvc.get().uri("/api/tesoreria/core/usuarioChequeraClaseChequera/user/3")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath("$").asArray().isEmpty();
    }

}
