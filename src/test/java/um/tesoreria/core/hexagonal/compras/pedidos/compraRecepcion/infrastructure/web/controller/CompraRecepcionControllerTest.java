package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.exception.CompraRecepcionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.service.CompraRecepcionService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.mapper.CompraRecepcionDtoMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(CompraRecepcionController.class)
class CompraRecepcionControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private CompraRecepcionService service;

    @MockitoBean
    private CompraRecepcionDtoMapper dtoMapper;

    private CompraRecepcion recepcion() {
        return CompraRecepcion.builder().compraRecepcionId(1L).numero("RC-2026-000001").build();
    }

    private CompraRecepcionResponse response() {
        return CompraRecepcionResponse.builder().compraRecepcionId(1L).numero("RC-2026-000001").build();
    }

    @Test
    void findAllDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(recepcion()));
        when(dtoMapper.toResponse(any(CompraRecepcion.class))).thenReturn(response());

        mockMvc.get().uri("/api/tesoreria/core/compraRecepcion?ejercicioId=15&resultado=conforme")
                .assertThat().hasStatusOk();
    }

    @Test
    void searchDevuelveLaLista() {
        when(service.listar(any())).thenReturn(List.of(recepcion()));
        when(dtoMapper.toResponse(any(CompraRecepcion.class))).thenReturn(response());

        mockMvc.post().uri("/api/tesoreria/core/compraRecepcion/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ejercicioId\":15,\"compraFormalizacionId\":50,\"resultado\":\"NO_CONFORME\"}")
                .assertThat().hasStatusOk();
    }

    @Test
    void pendientesDevuelveLaLista() {
        when(service.pendientes(15)).thenReturn(List.of());
        mockMvc.get().uri("/api/tesoreria/core/compraRecepcion/pendientes?ejercicioId=15")
                .assertThat().hasStatusOk();
    }

    @Test
    void pendientesMapeaLaRespuesta() {
        when(service.pendientes(15)).thenReturn(List.of(new CompraRecepcionPendiente(50L, "OC-2026-000003",
                null, 15, 7, null, null, null, List.of(), List.of())));
        when(dtoMapper.toResponse(any(CompraRecepcionPendiente.class)))
                .thenReturn(CompraRecepcionPendienteResponse.builder().compraFormalizacionId(50L).build());

        mockMvc.get().uri("/api/tesoreria/core/compraRecepcion/pendientes?ejercicioId=15")
                .assertThat().hasStatusOk();
    }

    @Test
    void porFormalizacionDevuelveLaOrdenDeCompraConSaldos() {
        when(service.porFormalizacion(50L)).thenReturn(Optional.of(new CompraRecepcionPendiente(50L,
                "OC-2026-000003", null, 15, 7, null, null, null, List.of(), List.of())));
        when(dtoMapper.toResponse(any(CompraRecepcionPendiente.class))).thenReturn(
                CompraRecepcionPendienteResponse.builder().compraFormalizacionId(50L).build());

        mockMvc.get().uri("/api/tesoreria/core/compraRecepcion/por-formalizacion/50")
                .assertThat().hasStatusOk();
    }

    @Test
    void porFormalizacionInexistenteDevuelve404() {
        when(service.porFormalizacion(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/compraRecepcion/por-formalizacion/99")
                .assertThat().hasStatus(404);
    }

    @Test
    void findByIdDevuelveLaRecepcion() {
        when(service.getById(1L)).thenReturn(Optional.of(recepcion()));
        when(dtoMapper.toResponse(any(CompraRecepcion.class))).thenReturn(response());

        mockMvc.get().uri("/api/tesoreria/core/compraRecepcion/1").assertThat().hasStatusOk();
    }

    @Test
    void findByIdInexistenteDevuelve404() {
        when(service.getById(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/compraRecepcion/99").assertThat().hasStatus(404);
    }

    @Test
    void registrarOk() {
        when(dtoMapper.toDomain(any(CompraRecepcionRequest.class))).thenReturn(CompraRecepcion.builder().build());
        when(service.registrar(eq(50L), any(), eq(38))).thenReturn(recepcion());
        when(dtoMapper.toResponse(any(CompraRecepcion.class))).thenReturn(response());

        mockMvc.post().uri("/api/tesoreria/core/compraRecepcion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"compraFormalizacionId\":50,\"usuarioId\":38,\"items\":[{\"compraFormalizacionItemId\":100,\"cantidadRecibida\":2}]}")
                .assertThat().hasStatusOk();
    }

    @Test
    void registrarSinOrdenDeCompraDevuelve400() {
        mockMvc.post().uri("/api/tesoreria/core/compraRecepcion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .assertThat().hasStatus(400);
    }

    @Test
    void registrarOrdenInexistenteDevuelve404() {
        when(dtoMapper.toDomain(any(CompraRecepcionRequest.class))).thenReturn(CompraRecepcion.builder().build());
        when(service.registrar(eq(50L), any(), any()))
                .thenThrow(new CompraRecepcionException("No existe la Orden de Compra 50"));

        mockMvc.post().uri("/api/tesoreria/core/compraRecepcion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"compraFormalizacionId\":50}")
                .assertThat().hasStatus(404);
    }

    @Test
    void registrarCantidadInvalidaDevuelve400() {
        when(dtoMapper.toDomain(any(CompraRecepcionRequest.class))).thenReturn(CompraRecepcion.builder().build());
        when(service.registrar(eq(50L), any(), any()))
                .thenThrow(new IllegalArgumentException("La cantidad recibida supera el saldo pendiente"));

        mockMvc.post().uri("/api/tesoreria/core/compraRecepcion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"compraFormalizacionId\":50}")
                .assertThat().hasStatus(400);
    }

    @Test
    void cerrarDevuelve204() {
        mockMvc.post().uri("/api/tesoreria/core/compraRecepcion/formalizacion/50/cerrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivo\":\"El proveedor no completará el resto\",\"usuarioId\":38}")
                .assertThat().hasStatus(204);
    }

    @Test
    void cerrarSinMotivoDevuelve400() {
        doThrow(new IllegalArgumentException("El cierre manual de la recepción requiere un motivo"))
                .when(service).cerrar(eq(50L), any(), any());

        mockMvc.post().uri("/api/tesoreria/core/compraRecepcion/formalizacion/50/cerrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}")
                .assertThat().hasStatus(400);
    }

    @Test
    void cerrarOrdenInexistenteDevuelve404() {
        doThrow(new CompraRecepcionException("No existe la Orden de Compra 99"))
                .when(service).cerrar(eq(99L), any(), any());

        mockMvc.post().uri("/api/tesoreria/core/compraRecepcion/formalizacion/99/cerrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"motivo\":\"motivo\"}")
                .assertThat().hasStatus(404);
    }

}
