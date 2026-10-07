package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.usuarios.rol.application.service.RolService;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.dto.RolResponse;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.mapper.RolDtoMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@WebMvcTest(RolController.class)
class RolControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private RolService service;

    @MockitoBean
    private RolDtoMapper dtoMapper;

    @Test
    void findAll_returnsList() {
        var domain = Rol.builder().rolId(1L).nombre("OPERADOR_CHEQUERAS").build();
        var response = RolResponse.builder().rolId(1L).nombre("OPERADOR_CHEQUERAS").build();
        when(service.findAll()).thenReturn(List.of(domain));
        when(dtoMapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/rol/")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].nombre").isEqualTo("OPERADOR_CHEQUERAS");
    }

    @Test
    void findById_whenMissing_returns404() {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/rol/99")
                .assertThat().hasStatus(404);
    }
}
