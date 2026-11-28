package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobDetalle;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionPadronItem;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.GeneracionBorradoresUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.mapper.GeneracionDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@WebMvcTest(GeneracionBorradoresController.class)
@Import(GeneracionDtoMapper.class)
class GeneracionBorradoresControllerTest {

    @Autowired
    MockMvcTester mvc;
    @MockitoBean
    GeneracionBorradoresUseCase useCase;

    @Test
    void previewDevuelveElPadron() {
        when(useCase.preview(any())).thenReturn(List.of(GeneracionPadronItem.builder()
                .chequeraId(10L)
                .personaId(new BigDecimal("1234567"))
                .documentoId(1)
                .personaNombre("Pérez, Ana")
                .tipoChequeraId(2)
                .tipoChequeraNombre("Grado")
                .chequeraSerieId(10L)
                .enviado((byte) 0)
                .borradorAbiertoId(55L)
                .build()));

        var respuesta = mvc.post().uri("/api/tesoreria/core/emision/chequera/generacion/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"lectivoId":38,"facultadId":5,"geograficaId":1,"soloNoEnviadas":true}
                        """)
                .assertThat().hasStatus(HttpStatus.OK).bodyJson();

        respuesta.extractingPath("$[0].chequeraId").isEqualTo(10);
        respuesta.extractingPath("$[0].personaNombre").isEqualTo("Pérez, Ana");
        respuesta.extractingPath("$[0].borradorAbiertoId").isEqualTo(55);
    }

    @Test
    void iniciarBorradoresDevuelveAccepted() {
        when(useCase.iniciarBorradores(any(), any())).thenReturn(GeneracionJob.builder()
                .jobId(42L)
                .etapa(GeneracionEtapa.BORRADORES)
                .estado(GeneracionJobEstado.PENDIENTE)
                .lectivoId(38).facultadId(5).geograficaId(1)
                .totalTareas(3).tareasPendientes(3)
                .build());

        var respuesta = mvc.post().uri("/api/tesoreria/core/emision/chequera/generacion/borradores")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"lectivoId":38,"facultadId":5,"geograficaId":1,"soloNoEnviadas":true}
                        """)
                .assertThat().hasStatus(HttpStatus.ACCEPTED).bodyJson();

        respuesta.extractingPath("$.jobId").isEqualTo(42);
        respuesta.extractingPath("$.etapa").isEqualTo("BORRADORES");
        respuesta.extractingPath("$.totalTareas").isEqualTo(3);
    }

    @Test
    void iniciarEmisionDirectaDevuelveAccepted() {
        when(useCase.iniciarEmisionDirecta(any(), any())).thenReturn(GeneracionJob.builder()
                .jobId(43L)
                .etapa(GeneracionEtapa.EMISION)
                .estado(GeneracionJobEstado.PENDIENTE)
                .lectivoId(38).facultadId(5).geograficaId(1)
                .totalTareas(2).tareasPendientes(2)
                .build());

        var respuesta = mvc.post().uri("/api/tesoreria/core/emision/chequera/generacion/emision")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"lectivoId":38,"facultadId":5,"geograficaId":1,"forzarDeuda":false}
                        """)
                .assertThat().hasStatus(HttpStatus.ACCEPTED).bodyJson();

        respuesta.extractingPath("$.jobId").isEqualTo(43);
        respuesta.extractingPath("$.etapa").isEqualTo("EMISION");
    }

    @Test
    void consultarDevuelveJobYTareas() {
        when(useCase.consultar(42L)).thenReturn(GeneracionJobDetalle.builder()
                .job(GeneracionJob.builder().jobId(42L).etapa(GeneracionEtapa.BORRADORES)
                        .estado(GeneracionJobEstado.COMPLETADO).build())
                .tareas(List.of(GeneracionJobTarea.builder()
                        .tareaId(1L).chequeraId(10L).borradorId(55L)
                        .personaNombre("Pérez, Ana").estado(GeneracionTareaEstado.BORRADOR_CREADO).build()))
                .build());

        var respuesta = mvc.get().uri("/api/tesoreria/core/emision/chequera/generacion/job/42")
                .assertThat().hasStatus(HttpStatus.OK).bodyJson();

        respuesta.extractingPath("$.job.jobId").isEqualTo(42);
        respuesta.extractingPath("$.tareas[0].borradorId").isEqualTo(55);
        respuesta.extractingPath("$.tareas[0].estado").isEqualTo("BORRADOR_CREADO");
    }

    @Test
    void iniciarEmisionDesdeElJobDevuelveAccepted() {
        when(useCase.iniciarEmision(eq(42L), any(), eq(false), isNull(), any())).thenReturn(GeneracionJob.builder()
                .jobId(44L).etapa(GeneracionEtapa.EMISION).jobOrigenId(42L)
                .estado(GeneracionJobEstado.PENDIENTE).totalTareas(1).tareasPendientes(1).build());

        var respuesta = mvc.post()
                .uri("/api/tesoreria/core/emision/chequera/generacion/job/42/emision")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"borradorIds":[55],"forzarDeuda":false}
                        """)
                .assertThat().hasStatus(HttpStatus.ACCEPTED).bodyJson();

        respuesta.extractingPath("$.jobOrigenId").isEqualTo(42);
    }
}
