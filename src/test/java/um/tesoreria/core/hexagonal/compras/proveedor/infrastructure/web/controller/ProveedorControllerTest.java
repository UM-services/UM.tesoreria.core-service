package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorConflictoException;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;
import um.tesoreria.core.model.PaginatedResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(ProveedorController.class)
@Import(ProveedorDtoMapper.class)
class ProveedorControllerTest {

    @Autowired
    private MockMvcTester mockMvc;
    @MockitoBean
    private ProveedorService service;

    @Test
    void laBajaDeUnProveedorLibreResponde204SinCuerpo() {
        when(service.delete(5)).thenReturn(true);

        mockMvc.delete().uri("/api/tesoreria/core/proveedor/5").assertThat().hasStatus(HttpStatus.NO_CONTENT);
    }

    @Test
    void laBajaDeUnProveedorInexistenteResponde404() {
        when(service.delete(5)).thenReturn(false);

        mockMvc.delete().uri("/proveedor/5").assertThat().hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void laBajaConRegistrosAsociadosResponde409ConUnCuerpoQueLoExplica() {
        when(service.delete(5)).thenThrow(new ProveedorConflictoException("No se puede eliminar el proveedor: tiene registros en bancaria."));

        var response = mockMvc.delete().uri("/api/tesoreria/core/proveedor/5").assertThat().hasStatus(HttpStatus.CONFLICT);

        response.bodyJson().extractingPath("$.status").isEqualTo(409);
        response.bodyJson().extractingPath("$.message").isEqualTo("No se puede eliminar el proveedor: tiene registros en bancaria.");
    }

    @Test
    void elAltaConCuitRepetidoResponde409() {
        when(service.create(any())).thenThrow(new ProveedorConflictoException("Ya existe un proveedor con el CUIT 30-12345678-1."));

        var response = mockMvc.post().uri("/proveedor/").contentType(MediaType.APPLICATION_JSON)
                .content("{\"cuit\":\"30-12345678-1\",\"numeroCuenta\":2110101}")
                .assertThat().hasStatus(HttpStatus.CONFLICT);

        response.bodyJson().extractingPath("$.status").isEqualTo(409);
    }

    @Test
    void laEdicionConCuitRepetidoResponde409() {
        when(service.update(any(), any())).thenThrow(new ProveedorConflictoException("Ya existe otro proveedor con el CUIT 30-99999999-9."));

        mockMvc.put().uri("/proveedor/5").contentType(MediaType.APPLICATION_JSON)
                .content("{\"cuit\":\"30-99999999-9\"}")
                .assertThat().hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void elListadoPaginadoSeConsultaPorPostConElCuerpoPageRequest() {
        when(service.getPaginated(1, 10)).thenReturn(new PaginatedResponse<>(List.of(), 0L, 0, 1, 10));

        mockMvc.post().uri("/proveedor/page").contentType(MediaType.APPLICATION_JSON)
                .content("{\"page\":1,\"size\":10}")
                .assertThat().hasStatus(HttpStatus.OK);
    }

    @Test
    void elListadoPaginadoSinCuerpoUsaPagina0Y50() {
        when(service.getPaginated(0, 50)).thenReturn(new PaginatedResponse<>(List.of(), 0L, 0, 0, 50));

        mockMvc.post().uri("/proveedor/page").assertThat().hasStatus(HttpStatus.OK);
    }
}