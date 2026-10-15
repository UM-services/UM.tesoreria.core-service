package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception.CompraAutoridadPerfilException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.service.CompraAutoridadPerfilService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.dto.CompraAutoridadPerfilResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.mapper.CompraAutoridadPerfilDtoMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(CompraAutoridadPerfilController.class)
class CompraAutoridadPerfilControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private CompraAutoridadPerfilService service;

    @MockitoBean
    private CompraAutoridadPerfilDtoMapper dtoMapper;

    private CompraAutoridadPerfil perfil() {
        return CompraAutoridadPerfil.builder().autoridadPerfilId(1L).nombre("N1").multiplico(3).build();
    }

    private CompraAutoridadPerfilResponse response() {
        return CompraAutoridadPerfilResponse.builder().autoridadPerfilId(1L).nombre("N1").multiplico(3).build();
    }

    @Test
    void findAllDevuelveLaLista() {
        CompraAutoridadPerfil p = perfil();
        when(service.findAll()).thenReturn(List.of(p));
        when(dtoMapper.toResponse(p)).thenReturn(response());

        mockMvc.get().uri("/api/tesoreria/core/compraAutoridadPerfil")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].nombre").isEqualTo("N1");
    }

    @Test
    void findByIdInexistenteDevuelve404() {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/compraAutoridadPerfil/99")
                .assertThat().hasStatus(404);
    }

    @Test
    void addDevuelveElPerfilCreado() {
        CompraAutoridadPerfil p = perfil();
        when(dtoMapper.toDomain(any())).thenReturn(p);
        when(service.add(any())).thenReturn(p);
        when(dtoMapper.toResponse(p)).thenReturn(response());

        mockMvc.post().uri("/api/tesoreria/core/compraAutoridadPerfil")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"N1\",\"multiplico\":3}")
                .assertThat().hasStatusOk();
    }

    @Test
    void addInvalidoDevuelve400() {
        when(dtoMapper.toDomain(any())).thenReturn(perfil());
        when(service.add(any())).thenThrow(new CompraAutoridadPerfilException("El múltiplo debe ser mayor o igual a 1"));

        mockMvc.post().uri("/api/tesoreria/core/compraAutoridadPerfil")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"N1\",\"multiplico\":0}")
                .assertThat().hasStatus(400);
    }

    @Test
    void updateOk() {
        CompraAutoridadPerfil p = perfil();
        when(dtoMapper.toDomain(any())).thenReturn(p);
        when(service.update(any(), org.mockito.ArgumentMatchers.eq(1L))).thenReturn(p);
        when(dtoMapper.toResponse(p)).thenReturn(response());

        mockMvc.put().uri("/api/tesoreria/core/compraAutoridadPerfil/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"N1\",\"multiplico\":3}")
                .assertThat().hasStatusOk();
    }

    @Test
    void updateConflictoDevuelve409() {
        when(dtoMapper.toDomain(any())).thenReturn(perfil());
        when(service.update(any(), org.mockito.ArgumentMatchers.eq(1L)))
                .thenThrow(new CompraAutoridadPerfilException("Ya existe"));

        mockMvc.put().uri("/api/tesoreria/core/compraAutoridadPerfil/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"N1\",\"multiplico\":3}")
                .assertThat().hasStatus(409);
    }

    @Test
    void deleteOk() {
        mockMvc.delete().uri("/api/tesoreria/core/compraAutoridadPerfil/1")
                .assertThat().hasStatus(204);
    }

    @Test
    void deleteConflictoDevuelve409() {
        doThrow(new CompraAutoridadPerfilException("tiene usuarios asignados"))
                .when(service).delete(1L);

        mockMvc.delete().uri("/api/tesoreria/core/compraAutoridadPerfil/1")
                .assertThat().hasStatus(409);
    }

}
