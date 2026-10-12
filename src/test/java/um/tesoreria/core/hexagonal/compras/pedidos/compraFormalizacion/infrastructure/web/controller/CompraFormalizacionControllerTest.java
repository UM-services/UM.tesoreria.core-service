package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.exception.CompraFormalizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.service.CompraFormalizacionService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.mapper.CompraFormalizacionDtoMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(CompraFormalizacionController.class)
class CompraFormalizacionControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private CompraFormalizacionService service;

    @MockitoBean
    private CompraFormalizacionDtoMapper dtoMapper;

    private CompraFormalizacion oc() {
        return CompraFormalizacion.builder().compraFormalizacionId(1L).numero("OC-2026-000001").build();
    }

    private CompraFormalizacionResponse response() {
        return CompraFormalizacionResponse.builder().compraFormalizacionId(1L).numero("OC-2026-000001").build();
    }

    @Test
    void findAllDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(oc()));
        when(dtoMapper.toResponse(any(CompraFormalizacion.class))).thenReturn(response());

        mockMvc.get().uri("/api/tesoreria/core/compraFormalizacion?ejercicioId=15")
                .assertThat().hasStatusOk();
    }

    @Test
    void pendientesDevuelveLaLista() {
        when(service.pendientes(15)).thenReturn(List.of());
        mockMvc.get().uri("/api/tesoreria/core/compraFormalizacion/pendientes?ejercicioId=15")
                .assertThat().hasStatusOk();
    }

    @Test
    void searchDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(oc()));
        when(dtoMapper.toResponse(any(CompraFormalizacion.class))).thenReturn(response());

        mockMvc.post().uri("/api/tesoreria/core/compraFormalizacion/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ejercicioId\":15}")
                .assertThat().hasStatusOk();
    }

    @Test
    void pendientesMapeaLaRespuesta() {
        when(service.pendientes(15)).thenReturn(List.of(new CompraFormalizacionPendiente(1L, "SC-1", 15,
                9L, 5L, 7, null, null, null, null, null, List.of())));
        when(dtoMapper.toResponse(any(CompraFormalizacionPendiente.class)))
                .thenReturn(CompraFormalizacionPendienteResponse.builder().compraCotizacionId(1L).build());

        mockMvc.get().uri("/api/tesoreria/core/compraFormalizacion/pendientes?ejercicioId=15")
                .assertThat().hasStatusOk();
    }

    @Test
    void porCotizacionDevuelveLaOc() {
        when(service.porCotizacion(1L)).thenReturn(Optional.of(oc()));
        when(dtoMapper.toResponse(any(CompraFormalizacion.class))).thenReturn(response());

        mockMvc.get().uri("/api/tesoreria/core/compraFormalizacion/por-cotizacion/1")
                .assertThat().hasStatusOk();
    }

    @Test
    void porCotizacionInexistenteDevuelve404() {
        when(service.porCotizacion(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/compraFormalizacion/por-cotizacion/99")
                .assertThat().hasStatus(404);
    }

    @Test
    void findByIdDevuelveLaOc() {
        when(service.getById(1L)).thenReturn(Optional.of(oc()));
        when(dtoMapper.toResponse(any(CompraFormalizacion.class))).thenReturn(response());

        mockMvc.get().uri("/api/tesoreria/core/compraFormalizacion/1")
                .assertThat().hasStatusOk();
    }

    @Test
    void findByIdInexistenteDevuelve404() {
        when(service.getById(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/compraFormalizacion/99")
                .assertThat().hasStatus(404);
    }

    @Test
    void formalizarOk() {
        when(dtoMapper.toDomain(any())).thenReturn(CompraFormalizacion.builder().build());
        when(service.formalizar(eq(1L), any(), eq(38))).thenReturn(oc());
        when(dtoMapper.toResponse(any(CompraFormalizacion.class))).thenReturn(response());

        mockMvc.post().uri("/api/tesoreria/core/compraFormalizacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"compraCotizacionId\":1,\"usuarioId\":38,\"numeroExterno\":\"OC-EXT\"}")
                .assertThat().hasStatusOk();
    }

    @Test
    void formalizarSinCotizacionDevuelve400() {
        mockMvc.post().uri("/api/tesoreria/core/compraFormalizacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .assertThat().hasStatus(400);
    }

    @Test
    void formalizarSinAdjudicacionDevuelve404() {
        when(dtoMapper.toDomain(any())).thenReturn(CompraFormalizacion.builder().build());
        when(service.formalizar(eq(1L), any(), any()))
                .thenThrow(new CompraFormalizacionException("sin adjudicación"));

        mockMvc.post().uri("/api/tesoreria/core/compraFormalizacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"compraCotizacionId\":1}")
                .assertThat().hasStatus(404);
    }

    @Test
    void formalizarConflictoDevuelve409() {
        when(dtoMapper.toDomain(any())).thenReturn(CompraFormalizacion.builder().build());
        when(service.formalizar(eq(1L), any(), any()))
                .thenThrow(new IllegalStateException("ya formalizada"));

        mockMvc.post().uri("/api/tesoreria/core/compraFormalizacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"compraCotizacionId\":1}")
                .assertThat().hasStatus(409);
    }
}
