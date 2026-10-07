package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.service.RolPermisoService;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.dto.RolPermisoResponse;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.mapper.RolPermisoDtoMapper;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(RolPermisoController.class)
class RolPermisoControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private RolPermisoService service;

    @MockitoBean
    private RolPermisoDtoMapper dtoMapper;

    @Test
    void findAllByRolId_returnsList() {
        var domain = RolPermiso.builder().rolPermisoId(1L).rolId(5L).permisoId(10L).build();
        var response = RolPermisoResponse.builder().rolPermisoId(1L).rolId(5L).permisoId(10L).build();
        when(service.findAllByRolId(5L)).thenReturn(List.of(domain));
        when(dtoMapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/rolPermiso/rol/5")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].permisoId").isEqualTo(10);
    }
}
