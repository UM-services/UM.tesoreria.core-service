package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.service.UsuarioRolService;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.dto.UsuarioRolResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.mapper.UsuarioRolDtoMapper;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(UsuarioRolController.class)
class UsuarioRolControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private UsuarioRolService service;

    @MockitoBean
    private UsuarioRolDtoMapper dtoMapper;

    @Test
    void findAllByUserId_returnsList() {
        var domain = UsuarioRol.builder().usuarioRolId(1L).userId(1L).rolId(5L).build();
        var response = UsuarioRolResponse.builder().usuarioRolId(1L).userId(1L).rolId(5L).build();
        when(service.findAllByUserId(1L)).thenReturn(List.of(domain));
        when(dtoMapper.toResponse(domain)).thenReturn(response);

        mockMvc.get().uri("/api/tesoreria/core/usuarioRol/user/1")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].rolId").isEqualTo(5);
    }
}
