package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException.Codigo;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.AsientoIngresoService;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoAccion;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobDetalle;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResumen;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResultado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.AsientoIngresoExceptionAdvice;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.mapper.AsientoIngresoDtoMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(AsientoIngresoController.class)
@Import({AsientoIngresoDtoMapper.class, AsientoIngresoExceptionAdvice.class})
class AsientoIngresoControllerTest {

    private static final String FECHA = "2026-11-17T00:00:00Z";
    private static final OffsetDateTime FECHA_ODT = OffsetDateTime.of(2026, 11, 17, 0, 0, 0, 0, ZoneOffset.UTC);

    @Autowired MockMvcTester mvc;
    @MockitoBean AsientoIngresoService service;

    private AsientoIngresoResumen resumen() {
        return AsientoIngresoResumen.builder()
                .fecha(FECHA_ODT)
                .tipoPagoId(3)
                .tipoPagoNombre("Efectivo")
                .existe(true)
                .ordenContable(7)
                .vinculo("Asiento Ingreso")
                .totalDebe(new BigDecimal("100.00"))
                .totalHaber(new BigDecimal("100.00"))
                .cuadra(true)
                .lineas(List.of())
                .build();
    }

    @Test
    void consultaDevuelveElResumen() {
        when(service.consultar(any(), eq(3))).thenReturn(resumen());

        mvc.get().uri("/asientoIngreso/" + FECHA + "/3").assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson().extractingPath("$.ordenContable").isEqualTo(7);
    }

    @Test
    void regenerarDevuelveAccionYFaltantes() {
        when(service.regenerar(any(), eq(3))).thenReturn(AsientoIngresoResultado.builder()
                .resumen(resumen())
                .accion(AsientoIngresoAccion.GENERADO)
                .faltantes(List.of())
                .build());

        mvc.post().uri("/api/tesoreria/core/asientoIngreso/" + FECHA + "/3/regenerar").assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson().extractingPath("$.accion").isEqualTo("GENERADO");
    }

    @Test
    void masivoDevuelve202() {
        when(service.iniciarMasiva(any(), any(), any())).thenReturn(AsientoIngresoJob.builder()
                .jobId(12L)
                .estado(AsientoIngresoJobEstado.PENDIENTE)
                .fechaDesde(FECHA_ODT)
                .fechaHasta(FECHA_ODT)
                .totalTareas(10)
                .tareasPendientes(10)
                .build());

        mvc.post().uri("/asientoIngreso/masivo/" + FECHA + "/" + FECHA).assertThat()
                .hasStatus(HttpStatus.ACCEPTED)
                .bodyJson().extractingPath("$.jobId").isEqualTo(12);
    }

    @Test
    void jobInexistenteDevuelve404ConCodigo() {
        when(service.consultarJob(99L)).thenThrow(new AsientoIngresoException(
                Codigo.JOB_NO_ENCONTRADO, "No existe el job 99"));

        mvc.get().uri("/asientoIngreso/masivo/99").assertThat()
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson().extractingPath("$.codigo").isEqualTo("JOB_NO_ENCONTRADO");
    }

    @Test
    void detalleDeJobDevuelveProgreso() {
        when(service.consultarJob(12L)).thenReturn(AsientoIngresoJobDetalle.builder()
                .job(AsientoIngresoJob.builder()
                        .jobId(12L)
                        .estado(AsientoIngresoJobEstado.COMPLETADO)
                        .fechaDesde(FECHA_ODT)
                        .fechaHasta(FECHA_ODT)
                        .totalTareas(2)
                        .tareasOk(2)
                        .build())
                .resumenPorFecha(List.of())
                .errores(List.of())
                .build());

        mvc.get().uri("/asientoIngreso/masivo/12").assertThat()
                .hasStatus(HttpStatus.OK)
                .bodyJson().extractingPath("$.estado").isEqualTo("COMPLETADO");
    }

}
