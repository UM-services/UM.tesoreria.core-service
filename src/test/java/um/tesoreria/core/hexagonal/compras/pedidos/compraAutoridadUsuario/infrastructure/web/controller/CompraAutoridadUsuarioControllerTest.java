package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.exception.CompraAutoridadUsuarioException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.service.CompraAutoridadUsuarioService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.model.LimiteAutorizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto.CompraAutoridadUsuarioResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto.LimiteAutorizacionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.mapper.CompraAutoridadUsuarioDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(CompraAutoridadUsuarioController.class)
class CompraAutoridadUsuarioControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private CompraAutoridadUsuarioService service;

    @MockitoBean
    private CompraAutoridadUsuarioDtoMapper dtoMapper;

    @Test
    void getPerfilesDevuelveLaAsignacion() {
        when(service.getPerfilIds(9)).thenReturn(List.of(5L));
        when(dtoMapper.toResponse(9, List.of(5L))).thenReturn(
                CompraAutoridadUsuarioResponse.builder().usuarioId(9).autoridadPerfilIds(List.of(5L)).build());

        mockMvc.get().uri("/api/tesoreria/core/compraAutoridadUsuario/9")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.usuarioId").isEqualTo(9);
    }

    @Test
    void getLimiteDevuelveElCalculo() {
        LimiteAutorizacion limite = new LimiteAutorizacion(9, 7, 3, new BigDecimal("1000"),
                new BigDecimal("3000"), false, true);
        when(service.getLimite(9, 7)).thenReturn(limite);
        when(dtoMapper.toResponse(limite)).thenReturn(LimiteAutorizacionResponse.builder()
                .usuarioId(9).limite(new BigDecimal("3000")).ilimitado(false).tieneAutoridad(true).build());

        mockMvc.get().uri("/api/tesoreria/core/compraAutoridadUsuario/limite/9/7")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.limite").isEqualTo(3000);
    }

    @Test
    void getLimiteInvalidoDevuelve400() {
        when(service.getLimite(any(), any()))
                .thenThrow(new CompraAutoridadUsuarioException("ejercicioId es requerido"));

        mockMvc.get().uri("/api/tesoreria/core/compraAutoridadUsuario/limite/9/7")
                .assertThat().hasStatus(400);
    }

    @Test
    void asignarDevuelveLaAsignacion() {
        when(dtoMapper.toResponse(eq(9), any())).thenReturn(
                CompraAutoridadUsuarioResponse.builder().usuarioId(9).autoridadPerfilIds(List.of(5L)).build());

        mockMvc.post().uri("/api/tesoreria/core/compraAutoridadUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuarioId\":9,\"autoridadPerfilId\":5}")
                .assertThat().hasStatusOk();
    }

    @Test
    void asignarInvalidoDevuelve400() {
        org.mockito.Mockito.doThrow(new CompraAutoridadUsuarioException("No existe el perfil"))
                .when(service).asignar(9, 5L);

        mockMvc.post().uri("/api/tesoreria/core/compraAutoridadUsuario")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"usuarioId\":9,\"autoridadPerfilId\":5}")
                .assertThat().hasStatus(400);
    }

    @Test
    void quitarDevuelveLaAsignacionRestante() {
        when(dtoMapper.toResponse(eq(9), any())).thenReturn(
                CompraAutoridadUsuarioResponse.builder().usuarioId(9).autoridadPerfilIds(List.of()).build());

        mockMvc.delete().uri("/api/tesoreria/core/compraAutoridadUsuario/9/5")
                .assertThat().hasStatusOk();
    }

}
