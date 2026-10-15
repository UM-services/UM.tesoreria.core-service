package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.service.PermisoService;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoResponse;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper.PermisoDtoMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@WebMvcTest(PermisoController.class)
class PermisoControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private PermisoService service;

    @MockitoBean
    private PermisoDtoMapper dtoMapper;

    @Test
    void findAll_returnsList() {
        var domain = Permiso.builder().permisoId(1L).clave("chequeras.eliminar").build();
        var response = PermisoResponse.builder().permisoId(1L).clave("chequeras.eliminar").build();
        when(service.findAll()).thenReturn(List.of(domain));
        when(dtoMapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/permiso/")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].clave").isEqualTo("chequeras.eliminar");
    }

    @Test
    void findById_whenMissing_returns404() {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/permiso/99")
                .assertThat().hasStatus(404);
    }
}
