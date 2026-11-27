package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException.Codigo;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.MovimientoConsultaService;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LibroDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LineaDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.MovimientoConsultaExceptionHandler;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.mapper.MovimientoConsultaDtoMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(MovimientoConsultaController.class)
@Import({MovimientoConsultaDtoMapper.class, MovimientoConsultaExceptionHandler.class})
class MovimientoConsultaControllerTest {

    static final String CUERPO = "{\"desde\":\"2025-03-01T00:00:00-03:00\",\"hasta\":\"2025-03-01T00:00:00-03:00\"}";
    static final OffsetDateTime DIA = OffsetDateTime.parse("2025-03-01T00:00:00Z");

    @Autowired MockMvcTester mockMvc;
    @MockitoBean MovimientoConsultaService service;

    LibroDiario libro() {
        LineaDiario linea = new LineaDiario(DIA, 1, 1, new BigDecimal("11101010001"), "Caja", "Factura B",
                "Proveedor Siete", "concepto", new BigDecimal("100.00"), null);
        AsientoDiario asiento = new AsientoDiario(DIA, 1, List.of(linea), new BigDecimal("100.00"), BigDecimal.ZERO);
        return new LibroDiario(DIA, DIA, List.of(asiento), new BigDecimal("100.00"), BigDecimal.ZERO);
    }

    @Test
    void libroDiarioPorRutaLarga() {
        when(service.consultarLibroDiario(any(), any())).thenReturn(libro());

        var respuesta = mockMvc.post().uri("/api/tesoreria/core/movimientoConsulta/libroDiario")
                .contentType(MediaType.APPLICATION_JSON).content(CUERPO);

        respuesta.assertThat().hasStatusOk().bodyJson()
                .hasPathSatisfying("$.asientos[0].ordenContable", v -> v.assertThat().isEqualTo(1))
                .hasPathSatisfying("$.asientos[0].lineas[0].nombreCuenta", v -> v.assertThat().isEqualTo("Caja"))
                .hasPathSatisfying("$.asientos[0].lineas[0].proveedor", v -> v.assertThat().isEqualTo("Proveedor Siete"))
                .hasPathSatisfying("$.asientos[0].lineas[0].debe", v -> v.assertThat().isEqualTo(100.00))
                .hasPathSatisfying("$.totalDebe", v -> v.assertThat().isEqualTo(100.00));
        verify(service).consultarLibroDiario(OffsetDateTime.parse("2025-03-01T00:00:00-03:00"),
                OffsetDateTime.parse("2025-03-01T00:00:00-03:00"));
    }

    @Test
    void conservaElOffsetRecibidoSinPasarloAUtc() {
        when(service.consultarLibroDiario(any(), any())).thenReturn(libro());

        mockMvc.post().uri("/api/tesoreria/core/movimientoConsulta/libroDiario")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"desde\":\"2025-03-01T22:00:00-03:00\",\"hasta\":\"2025-03-01T22:00:00-03:00\"}")
                .assertThat().hasStatusOk();

        var esperado = OffsetDateTime.parse("2025-03-01T22:00:00-03:00");
        verify(service).consultarLibroDiario(
                org.mockito.ArgumentMatchers.argThat(f -> f.getOffset().equals(esperado.getOffset())
                        && f.toLocalDate().equals(esperado.toLocalDate())),
                org.mockito.ArgumentMatchers.argThat(f -> f.getOffset().equals(esperado.getOffset())));
    }

    @Test
    void libroDiarioPorRutaCorta() {
        when(service.consultarLibroDiario(any(), any())).thenReturn(libro());

        mockMvc.post().uri("/movimientoConsulta/libroDiario")
                .contentType(MediaType.APPLICATION_JSON).content(CUERPO)
                .assertThat().hasStatusOk();
    }

    @Test
    void sinCuerpoEsFechasRequeridas() {
        when(service.consultarLibroDiario(isNull(), isNull()))
                .thenThrow(new MovimientoConsultaException(Codigo.FECHAS_REQUERIDAS, "Desde y hasta son obligatorios"));

        mockMvc.post().uri("/api/tesoreria/core/movimientoConsulta/libroDiario")
                .contentType(MediaType.APPLICATION_JSON)
                .assertThat().hasStatus(400).bodyJson()
                .hasPathSatisfying("$.codigo", v -> v.assertThat().isEqualTo("FECHAS_REQUERIDAS"))
                .hasPathSatisfying("$.status", v -> v.assertThat().isEqualTo(400));
    }

    @Test
    void desdePosteriorYTopeSon409() {
        when(service.consultarLibroDiario(any(), any()))
                .thenThrow(new MovimientoConsultaException(Codigo.DESDE_POSTERIOR, "x"))
                .thenThrow(new MovimientoConsultaException(Codigo.DEMASIADOS_MOVIMIENTOS, "y"));

        mockMvc.post().uri("/api/tesoreria/core/movimientoConsulta/libroDiario")
                .contentType(MediaType.APPLICATION_JSON).content(CUERPO)
                .assertThat().hasStatus(409).bodyJson()
                .hasPathSatisfying("$.codigo", v -> v.assertThat().isEqualTo("DESDE_POSTERIOR"));
        mockMvc.post().uri("/api/tesoreria/core/movimientoConsulta/libroDiario")
                .contentType(MediaType.APPLICATION_JSON).content(CUERPO)
                .assertThat().hasStatus(409).bodyJson()
                .hasPathSatisfying("$.codigo", v -> v.assertThat().isEqualTo("DEMASIADOS_MOVIMIENTOS"))
                .hasPathSatisfying("$.error", v -> v.assertThat().isEqualTo("Conflict"));
    }

    @Test
    void cuerpoIlegibleEs400ConCodigo() {
        mockMvc.post().uri("/api/tesoreria/core/movimientoConsulta/libroDiario")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"desde\":\"2025-03-01\",\"hasta\":\"2025-03-01\"}")
                .assertThat().hasStatus(400).bodyJson()
                .hasPathSatisfying("$.codigo", v -> v.assertThat().isEqualTo("CUERPO_INVALIDO"))
                .hasPathSatisfying("$.status", v -> v.assertThat().isEqualTo(400))
                .hasPathSatisfying("$.error", v -> v.assertThat().isEqualTo("Bad Request"));
        org.mockito.Mockito.verifyNoInteractions(service);
    }

    AsientoDiario asiento() {
        return libro().asientos().getFirst();
    }

    @Test
    void asientoPorRutaLargaConservaElOffset() {
        when(service.consultarAsiento(any(), any())).thenReturn(asiento());

        mockMvc.get().uri("/api/tesoreria/core/movimientoConsulta/asiento/2025-03-01T00:00:00-03:00/1")
                .assertThat().hasStatusOk().bodyJson()
                .hasPathSatisfying("$.ordenContable", v -> v.assertThat().isEqualTo(1))
                .hasPathSatisfying("$.lineas[0].nombreCuenta", v -> v.assertThat().isEqualTo("Caja"))
                .hasPathSatisfying("$.lineas[0].proveedor", v -> v.assertThat().isEqualTo("Proveedor Siete"))
                .hasPathSatisfying("$.totalDebe", v -> v.assertThat().isEqualTo(100.00));
        verify(service).consultarAsiento(org.mockito.ArgumentMatchers.argThat(f -> f.toLocalDate().toString()
                .equals("2025-03-01") && f.getOffset().getTotalSeconds() == -3 * 3600), org.mockito.ArgumentMatchers.eq(1));
    }

    @Test
    void asientoPorRutaCortaConZ() {
        when(service.consultarAsiento(any(), any())).thenReturn(asiento());

        mockMvc.get().uri("/movimientoConsulta/asiento/2025-03-01T00:00:00Z/1")
                .assertThat().hasStatusOk();
        verify(service).consultarAsiento(OffsetDateTime.parse("2025-03-01T00:00:00Z"), 1);
    }

    @Test
    void asientoInexistenteEs404YOrdenInvalidoEs409() {
        when(service.consultarAsiento(any(), any()))
                .thenThrow(new MovimientoConsultaException(Codigo.ASIENTO_INEXISTENTE, "x"))
                .thenThrow(new MovimientoConsultaException(Codigo.ORDEN_INVALIDO, "y"));

        mockMvc.get().uri("/api/tesoreria/core/movimientoConsulta/asiento/2025-03-01T00:00:00Z/9")
                .assertThat().hasStatus(404).bodyJson()
                .hasPathSatisfying("$.codigo", v -> v.assertThat().isEqualTo("ASIENTO_INEXISTENTE"))
                .hasPathSatisfying("$.error", v -> v.assertThat().isEqualTo("Not Found"));
        mockMvc.get().uri("/api/tesoreria/core/movimientoConsulta/asiento/2025-03-01T00:00:00Z/0")
                .assertThat().hasStatus(409).bodyJson()
                .hasPathSatisfying("$.codigo", v -> v.assertThat().isEqualTo("ORDEN_INVALIDO"));
    }

    @Test
    void asientoConFechaSinHoraUOrdenNoNumericoEs400() {
        mockMvc.get().uri("/api/tesoreria/core/movimientoConsulta/asiento/2025-03-01/1")
                .assertThat().hasStatus(400).bodyJson()
                .hasPathSatisfying("$.codigo", v -> v.assertThat().isEqualTo("PARAMETRO_INVALIDO"));
        mockMvc.get().uri("/api/tesoreria/core/movimientoConsulta/asiento/2025-03-01T00:00:00Z/abc")
                .assertThat().hasStatus(400).bodyJson()
                .hasPathSatisfying("$.codigo", v -> v.assertThat().isEqualTo("PARAMETRO_INVALIDO"));
        org.mockito.Mockito.verifyNoInteractions(service);
    }

    @Test
    void fechaFueraDeRangoEs400YRangoLargoEs409() {
        when(service.consultarLibroDiario(any(), any()))
                .thenThrow(new MovimientoConsultaException(Codigo.FECHA_FUERA_DE_RANGO, "x"))
                .thenThrow(new MovimientoConsultaException(Codigo.valueOf("RANGO_DEMASIADO_LARGO"), "y"));

        mockMvc.post().uri("/api/tesoreria/core/movimientoConsulta/libroDiario")
                .contentType(MediaType.APPLICATION_JSON).content(CUERPO)
                .assertThat().hasStatus(400).bodyJson()
                .hasPathSatisfying("$.codigo", v -> v.assertThat().isEqualTo("FECHA_FUERA_DE_RANGO"));
        mockMvc.post().uri("/api/tesoreria/core/movimientoConsulta/libroDiario")
                .contentType(MediaType.APPLICATION_JSON).content(CUERPO)
                .assertThat().hasStatus(409).bodyJson()
                .hasPathSatisfying("$.codigo", v -> v.assertThat().isEqualTo("RANGO_DEMASIADO_LARGO"));
    }
}
