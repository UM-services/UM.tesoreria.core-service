package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.service.UsuarioPermisoService;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.dto.UsuarioPermisoResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.mapper.UsuarioPermisoDtoMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(UsuarioPermisoController.class)
class UsuarioPermisoControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private UsuarioPermisoService service;

    @MockitoBean
    private UsuarioPermisoDtoMapper dtoMapper;

    @Test
    void findAllByUserId_returnsList() {
        var domain = UsuarioPermiso.builder().usuarioPermisoId(1L).userId(1L).permisoId(10L).otorgado((byte) 1).build();
        var response = UsuarioPermisoResponse.builder().usuarioPermisoId(1L).userId(1L).permisoId(10L).otorgado((byte) 1).build();
        when(service.findAllByUserId(1L)).thenReturn(List.of(domain));
        when(dtoMapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/usuarioPermiso/user/1")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].otorgado").isEqualTo(1);
    }

    @Test
    void set_returnsOverride() {
        var domain = UsuarioPermiso.builder().usuarioPermisoId(1L).userId(1L).permisoId(10L).otorgado((byte) 0).build();
        var response = UsuarioPermisoResponse.builder().usuarioPermisoId(1L).userId(1L).permisoId(10L).otorgado((byte) 0).build();
        when(service.set(eq(1L), eq(10L), any())).thenReturn(domain);
        when(dtoMapper.toResponse(domain)).thenReturn(response);

        mockMvc.put().uri("/api/tesoreria/core/usuarioPermiso/user/1/permiso/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"otorgado\":0}")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.otorgado").isEqualTo(0);
    }
}
