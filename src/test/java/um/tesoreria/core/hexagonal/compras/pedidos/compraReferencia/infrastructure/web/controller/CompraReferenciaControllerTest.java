package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.exception.CompraReferenciaException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.service.CompraReferenciaService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.dto.CompraReferenciaRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.dto.CompraReferenciaResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.mapper.CompraReferenciaDtoMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(CompraReferenciaController.class)
class CompraReferenciaControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private CompraReferenciaService service;

    @MockitoBean
    private CompraReferenciaDtoMapper dtoMapper;

    @Test
    void getDevuelveLaReferencia() {
        CompraReferencia domain = CompraReferencia.builder().ejercicioId(7).importe(new BigDecimal("100")).build();
        when(service.getByEjercicioId(7)).thenReturn(Optional.of(domain));
        when(dtoMapper.toResponse(domain)).thenReturn(
                CompraReferenciaResponse.builder().ejercicioId(7).importe(new BigDecimal("100")).build());

        mockMvc.get().uri("/api/tesoreria/core/compraReferencia/7")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.ejercicioId").isEqualTo(7);
    }

    @Test
    void getInexistenteDevuelve404() {
        when(service.getByEjercicioId(7)).thenReturn(Optional.empty());

        mockMvc.get().uri("/api/tesoreria/core/compraReferencia/7")
                .assertThat().hasStatus(404);
    }

    @Test
    void putGuardaLaReferencia() {
        CompraReferencia domain = CompraReferencia.builder().ejercicioId(7).importe(new BigDecimal("100")).build();
        when(dtoMapper.toDomain(eq(7), any(CompraReferenciaRequest.class))).thenReturn(domain);
        when(service.upsert(domain)).thenReturn(domain);
        when(dtoMapper.toResponse(domain)).thenReturn(
                CompraReferenciaResponse.builder().ejercicioId(7).importe(new BigDecimal("100")).build());

        mockMvc.put().uri("/api/tesoreria/core/compraReferencia/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"importe\":100}")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.importe").isEqualTo(100);
    }

    @Test
    void putInvalidoDevuelve400() {
        when(dtoMapper.toDomain(eq(7), any(CompraReferenciaRequest.class)))
                .thenReturn(CompraReferencia.builder().ejercicioId(7).build());
        when(service.upsert(any())).thenThrow(new CompraReferenciaException("El importe de referencia debe ser mayor a cero"));

        mockMvc.put().uri("/api/tesoreria/core/compraReferencia/7")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"importe\":0}")
                .assertThat().hasStatus(400);
    }

}
