package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception.CompraCotizacionTramoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.service.CompraCotizacionTramoService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto.CompraCotizacionTramoResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.mapper.CompraCotizacionTramoDtoMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@WebMvcTest(CompraCotizacionTramoController.class)
class CompraCotizacionTramoControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private CompraCotizacionTramoService service;

    @MockitoBean
    private CompraCotizacionTramoDtoMapper dtoMapper;

    private CompraCotizacionTramo tramo() {
        return CompraCotizacionTramo.builder().cotizacionTramoId(1L).nombre("N1").topeMultiplico(2).cantidadMinima(1).build();
    }

    private CompraCotizacionTramoResponse response() {
        return CompraCotizacionTramoResponse.builder().cotizacionTramoId(1L).nombre("N1").topeMultiplico(2).cantidadMinima(1).build();
    }

    @Test
    void findAllDevuelveLaLista() {
        CompraCotizacionTramo t = tramo();
        when(service.findAll()).thenReturn(List.of(t));
        when(dtoMapper.toResponse(t)).thenReturn(response());

        mockMvc.get().uri("/api/tesoreria/core/compraCotizacionTramo")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].nombre").isEqualTo("N1");
    }

    @Test
    void findByIdInexistenteDevuelve404() {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/compraCotizacionTramo/99")
                .assertThat().hasStatus(404);
    }

    @Test
    void findByIdDevuelveElTramo() {
        when(service.findById(1L)).thenReturn(Optional.of(tramo()));
        when(dtoMapper.toResponse(any())).thenReturn(response());

        mockMvc.get().uri("/api/tesoreria/core/compraCotizacionTramo/1")
                .assertThat().hasStatusOk();
    }

    @Test
    void addDevuelveElTramoCreado() {
        CompraCotizacionTramo t = tramo();
        when(dtoMapper.toDomain(any())).thenReturn(t);
        when(service.add(any())).thenReturn(t);
        when(dtoMapper.toResponse(t)).thenReturn(response());

        mockMvc.post().uri("/api/tesoreria/core/compraCotizacionTramo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"N1\",\"topeMultiplico\":2,\"cantidadMinima\":1}")
                .assertThat().hasStatusOk();
    }

    @Test
    void addInvalidoDevuelve400() {
        when(dtoMapper.toDomain(any())).thenReturn(tramo());
        when(service.add(any())).thenThrow(new CompraCotizacionTramoException("La cantidad mínima debe estar entre 1 y 10"));

        mockMvc.post().uri("/api/tesoreria/core/compraCotizacionTramo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"N1\",\"topeMultiplico\":2,\"cantidadMinima\":0}")
                .assertThat().hasStatus(400);
    }

    @Test
    void updateOk() {
        CompraCotizacionTramo t = tramo();
        when(dtoMapper.toDomain(any())).thenReturn(t);
        when(service.update(any(), eq(1L))).thenReturn(t);
        when(dtoMapper.toResponse(t)).thenReturn(response());

        mockMvc.put().uri("/api/tesoreria/core/compraCotizacionTramo/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"N1\",\"topeMultiplico\":2,\"cantidadMinima\":1}")
                .assertThat().hasStatusOk();
    }

    @Test
    void updateConflictoDevuelve409() {
        when(dtoMapper.toDomain(any())).thenReturn(tramo());
        when(service.update(any(), eq(1L))).thenThrow(new CompraCotizacionTramoException("Ya existe un tramo con tope 2"));

        mockMvc.put().uri("/api/tesoreria/core/compraCotizacionTramo/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"N1\",\"topeMultiplico\":2,\"cantidadMinima\":1}")
                .assertThat().hasStatus(409);
    }

    @Test
    void deleteOk() {
        mockMvc.delete().uri("/api/tesoreria/core/compraCotizacionTramo/1")
                .assertThat().hasStatus(204);
    }

    @Test
    void deleteConflictoDevuelve409() {
        doThrow(new CompraCotizacionTramoException("No existe"))
                .when(service).delete(1L);

        mockMvc.delete().uri("/api/tesoreria/core/compraCotizacionTramo/1")
                .assertThat().hasStatus(409);
    }

    @Test
    void minimoResuelveElTramo() {
        when(service.resolverMinimo(eq(15), any())).thenReturn(Optional.of(2));

        mockMvc.get().uri("/api/tesoreria/core/compraCotizacionTramo/minimo?ejercicioId=15&monto=150000")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.cantidadMinima").isEqualTo(2);
    }

    @Test
    void minimoSinConfiguracionDevuelve409() {
        when(service.resolverMinimo(eq(15), any())).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/compraCotizacionTramo/minimo?ejercicioId=15&monto=150000")
                .assertThat().hasStatus(409);
    }

}
