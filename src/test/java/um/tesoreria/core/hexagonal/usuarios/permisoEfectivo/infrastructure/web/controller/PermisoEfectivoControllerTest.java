package um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.exception.PermisoEfectivoException;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.service.PermisoEfectivoService;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.model.PermisoEfectivo;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.infrastructure.web.dto.PermisoEfectivoResponse;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.infrastructure.web.mapper.PermisoEfectivoDtoMapper;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(PermisoEfectivoController.class)
class PermisoEfectivoControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private PermisoEfectivoService service;

    @MockitoBean
    private PermisoEfectivoDtoMapper dtoMapper;

    @Test
    void getPermisosEfectivos_returnsBundle() {
        var domain = PermisoEfectivo.builder().userId(1L).permisos(List.of("chequeras.imprimir")).build();
        var response = PermisoEfectivoResponse.builder().userId(1L).permisos(List.of("chequeras.imprimir")).build();
        when(service.getPermisosEfectivos(1L)).thenReturn(domain);
        when(dtoMapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/permisoEfectivo/usuario/1")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.permisos[0]").isEqualTo("chequeras.imprimir");
    }

    @Test
    void getPermisosEfectivos_whenUsuarioMissing_returns404() {
        when(service.getPermisosEfectivos(99L)).thenThrow(new PermisoEfectivoException("no existe"));

        mockMvc.get().uri("/api/tesoreria/core/permisoEfectivo/usuario/99")
                .assertThat().hasStatus(404);
    }
}
