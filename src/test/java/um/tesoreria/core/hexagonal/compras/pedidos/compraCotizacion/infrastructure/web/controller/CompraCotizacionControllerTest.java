package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.service.CompraCotizacionService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacionDecision;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraAdjudicacionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.mapper.CompraCotizacionDtoMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(CompraCotizacionController.class)
class CompraCotizacionControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private CompraCotizacionService service;

    @MockitoBean
    private CompraCotizacionDtoMapper dtoMapper;

    private CompraCotizacion proceso() {
        return CompraCotizacion.builder().compraCotizacionId(1L).ejercicioId(15)
                .estado(CompraCotizacionEstado.BORRADOR).build();
    }

    private CompraCotizacionResponse response() {
        return new CompraCotizacionResponse(1L, null, 15, null, "BORRADOR", null, null, null, 2,
                null, null, null, null, null, null, List.of(), List.of());
    }

    @Test
    void findAllDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(proceso()));
        when(dtoMapper.toResponse(any(CompraCotizacion.class))).thenReturn(response());

        mockMvc.get().uri("/api/tesoreria/core/compraCotizacion")
                .assertThat().hasStatusOk();
    }

    @Test
    void findByIdInexistenteDevuelve404() {
        when(service.getById(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/compraCotizacion/99")
                .assertThat().hasStatus(404);
    }

    @Test
    void findByIdDevuelveElProceso() {
        when(service.getById(1L)).thenReturn(Optional.of(proceso()));
        when(dtoMapper.toResponse(any(CompraCotizacion.class))).thenReturn(response());

        mockMvc.get().uri("/api/tesoreria/core/compraCotizacion/1")
                .assertThat().hasStatusOk();
    }

    @Test
    void searchDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of());
        mockMvc.post().uri("/api/tesoreria/core/compraCotizacion/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"BORRADOR\"}")
                .assertThat().hasStatusOk();
    }

    @Test
    void pendientesDevuelveLaLista() {
        when(service.pendientes(eq(15), any())).thenReturn(List.of());
        mockMvc.get().uri("/api/tesoreria/core/compraCotizacion/pendientes?ejercicioId=15")
                .assertThat().hasStatusOk();
    }

    @Test
    void proveedoresPendientesDevuelveLaLista() {
        when(service.respuestasPendientes(eq(15))).thenReturn(List.of());
        mockMvc.get().uri("/api/tesoreria/core/compraCotizacion/proveedores-pendientes?ejercicioId=15")
                .assertThat().hasStatusOk();
    }

    @Test
    void createDevuelveElProceso() {
        when(dtoMapper.toDomain(any())).thenReturn(proceso());
        when(service.crear(any())).thenReturn(proceso());
        when(dtoMapper.toResponse(any(CompraCotizacion.class))).thenReturn(response());

        mockMvc.post().uri("/api/tesoreria/core/compraCotizacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ejercicioId\":15}")
                .assertThat().hasStatusOk();
    }

    @Test
    void createInvalidoDevuelve400() {
        when(dtoMapper.toDomain(any())).thenReturn(proceso());
        when(service.crear(any())).thenThrow(new CompraCotizacionException("sin ejercicio"));

        mockMvc.post().uri("/api/tesoreria/core/compraCotizacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .assertThat().hasStatus(400);
    }

    @Test
    void updateOk() {
        when(dtoMapper.toDomain(any())).thenReturn(proceso());
        when(service.actualizar(eq(1L), any())).thenReturn(proceso());
        when(dtoMapper.toResponse(any(CompraCotizacion.class))).thenReturn(response());

        mockMvc.put().uri("/api/tesoreria/core/compraCotizacion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .assertThat().hasStatusOk();
    }

    @Test
    void updateConflictoDevuelve409() {
        when(dtoMapper.toDomain(any())).thenReturn(proceso());
        when(service.actualizar(eq(1L), any())).thenThrow(new IllegalStateException("sólo borrador"));

        mockMvc.put().uri("/api/tesoreria/core/compraCotizacion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .assertThat().hasStatus(409);
    }

    @Test
    void enviarOk() {
        when(service.enviar(1L)).thenReturn(proceso());
        when(dtoMapper.toResponse(any(CompraCotizacion.class))).thenReturn(response());

        mockMvc.post().uri("/api/tesoreria/core/compraCotizacion/1/enviar")
                .assertThat().hasStatusOk();
    }

    @Test
    void enviarConflictoDevuelve409() {
        when(service.enviar(1L)).thenThrow(new IllegalStateException("falta el mínimo"));

        mockMvc.post().uri("/api/tesoreria/core/compraCotizacion/1/enviar")
                .assertThat().hasStatus(409);
    }

    @Test
    void anularOk() {
        when(service.anular(eq(1L), any())).thenReturn(proceso());
        when(dtoMapper.toResponse(any(CompraCotizacion.class))).thenReturn(response());

        mockMvc.post().uri("/api/tesoreria/core/compraCotizacion/1/anular")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivo\":\"x\"}")
                .assertThat().hasStatusOk();
    }

    @Test
    void adjudicarOk() {
        when(service.adjudicar(eq(1L), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(CompraAdjudicacion.builder().decision(CompraAdjudicacionDecision.ADJUDICADA).build());
        when(dtoMapper.toResponse(any(CompraAdjudicacion.class))).thenReturn(
                new CompraAdjudicacionResponse(1L, 1L, 15, 5L, 7, null, "ADJUDICADA", "f", 38L, null, null));

        mockMvc.post().uri("/api/tesoreria/core/compraCotizacion/1/adjudicar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"decision\":\"ADJUDICADA\",\"compraCotizacionProveedorId\":5,\"proveedorAdjudicadoId\":7}")
                .assertThat().hasStatusOk();
    }

    @Test
    void adjudicarConflictoDevuelve409() {
        when(service.adjudicar(eq(1L), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("no recomendada"));

        mockMvc.post().uri("/api/tesoreria/core/compraCotizacion/1/adjudicar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"decision\":\"ADJUDICADA\"}")
                .assertThat().hasStatus(409);
    }

    @Test
    void adjudicacionInexistenteDevuelve404() {
        when(service.adjudicacion(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/compraCotizacion/99/adjudicacion")
                .assertThat().hasStatus(404);
    }
}
