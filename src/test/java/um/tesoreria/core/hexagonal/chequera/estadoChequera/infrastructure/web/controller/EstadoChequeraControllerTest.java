package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.application.service.EstadoChequeraService;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.EstadoChequera;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.CuotaEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.DebitoEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.ProductoEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.mapper.EstadoChequeraDtoMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(EstadoChequeraController.class)
@Import(EstadoChequeraDtoMapper.class)
class EstadoChequeraControllerTest {

    private static final String URL = "/api/tesoreria/core/chequera/estado/1/2/12345/1/2";

    @Autowired
    private MockMvcTester mockMvc;
    @MockitoBean
    private EstadoChequeraService service;

    @Test
    void exposesTheEstadoChequeraWithTheContractReportServiceExpects() {
        CuotaEstado cuota = new CuotaEstado(1, 6, 2026, LocalDate.of(2026, 6, 10), new BigDecimal("182000.00"),
                0, LocalDate.of(2026, 6, 19), new BigDecimal("182000.00"), "D2026062301_30000000000");
        ProductoEstado producto = new ProductoEstado(1, "Matrícula", "Matrícula", 2,
                new BigDecimal("375000.00"), new BigDecimal("182000.00"), List.of(cuota));
        DebitoEstado debito = new DebitoEstado(5, new BigDecimal("364000.00"), LocalDate.of(2026, 7, 22),
                "1234567890123456789012", LocalDateTime.of(2026, 7, 3, 13, 30), true, "0011000100");
        when(service.getEstadoChequera(1, 2, 12345L, 1, 2)).thenReturn(new EstadoChequera(1, "Facultad de Ingeniería", 2,
                "Matrícula y Arancel", 12345L, new BigDecimal("12345678"), "MUÑOZ", "Ana Ejemplo", "Ciclo Completo",
                "Lectivo 2026 - 2027", new BigDecimal("0.15"), "Rapipago", 1, List.of(producto), List.of(debito)));

        var response = mockMvc.get().uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk();

        response.bodyJson().extractingPath("$.facultadNombre").isEqualTo("Facultad de Ingeniería");
        response.bodyJson().extractingPath("$.personaId").isEqualTo(12345678);
        response.bodyJson().extractingPath("$.personaApellido").isEqualTo("MUÑOZ");
        response.bodyJson().extractingPath("$.tipoImpresionNombre").isEqualTo("Rapipago");
        response.bodyJson().extractingPath("$.becaPorcentaje").isEqualTo(0.15);
        response.bodyJson().extractingPath("$.alternativaId").isEqualTo(1);
        response.bodyJson().extractingPath("$.productos[0].nombre").isEqualTo("Matrícula");
        response.bodyJson().extractingPath("$.productos[0].tituloCuota").isEqualTo("Matrícula");
        response.bodyJson().extractingPath("$.productos[0].totalCuotas").isEqualTo(2);
        response.bodyJson().extractingPath("$.productos[0].total").isEqualTo(375000.0);
        response.bodyJson().extractingPath("$.productos[0].cuotas[0].primerVencimiento").isEqualTo("2026-06-10");
        response.bodyJson().extractingPath("$.productos[0].cuotas[0].fechaPago").isEqualTo("2026-06-19");
        response.bodyJson().extractingPath("$.productos[0].cuotas[0].referenciaPago").isEqualTo("D2026062301_30000000000");
        response.bodyJson().extractingPath("$.debitos[0].fechaVencimiento").isEqualTo("2026-07-22");
        response.bodyJson().extractingPath("$.debitos[0].fechaEnvio").isEqualTo("2026-07-03T13:30:00");
        response.bodyJson().extractingPath("$.debitos[0].rechazado").isEqualTo(true);
        response.bodyJson().extractingPath("$.debitos[0].motivoRechazo").isEqualTo("0011000100");
    }

    @Test
    void respondsBadRequestWhenTheChequeraDoesNotExist() {
        when(service.getEstadoChequera(1, 2, 12345L, 1, 2)).thenThrow(new ChequeraSerieException(1, 2, 12345L));

        mockMvc.get().uri(URL)
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatus(400);
    }
}