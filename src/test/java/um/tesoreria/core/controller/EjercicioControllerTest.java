package um.tesoreria.core.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioNoEncontradoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.CreateEjercicioUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.DeleteEjercicioUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.UpdateEjercicioUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.mapper.EjercicioDtoMapper;
import um.tesoreria.core.service.EjercicioService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(EjercicioController.class)
@Import({EjercicioDtoMapper.class, EjercicioExceptionAdvice.class})
class EjercicioControllerTest {
    @Autowired MockMvcTester mvc;
    @MockitoBean EjercicioService service;
    @MockitoBean CreateEjercicioUseCase create;
    @MockitoBean UpdateEjercicioUseCase update;
    @MockitoBean DeleteEjercicioUseCase delete;

    static final String PAYLOAD_VB6 = """
            {"ejercicioId":15,"nombre":"Ejercicio Económico 2026","fechaInicio":"2026-03-01T00:00:00Z",
             "fechaFinal":"2027-02-28T00:00:00Z","bloqueado":1,"ordenContableResultado":7,"ordenContableBienesUso":9}""";

    Ejercicio ejercicio() {
        return Ejercicio.builder().ejercicioId(15).nombre("Ejercicio Económico 2026")
                .fechaInicio(LocalDate.parse("2026-03-01")).fechaFinal(LocalDate.parse("2027-02-28")).bloqueado((byte) 1)
                .ordenContableResultado(7).ordenContableBienesUso(9).build();
    }

    @Test
    void putConPayloadDeVb6DelegaAlCasoDeUsoYConservaElContratoDeRespuesta() {
        when(update.updateEjercicio(eq(15), any())).thenReturn(ejercicio());
        var respuesta = mvc.put().uri("/ejercicio/15").contentType(MediaType.APPLICATION_JSON).content(PAYLOAD_VB6)
                .assertThat().hasStatus(HttpStatus.OK).bodyJson();
        respuesta.extractingPath("$.ejercicioId").isEqualTo(15);
        respuesta.extractingPath("$.fechaInicio").isEqualTo("2026-03-01T00:00:00Z");
        respuesta.extractingPath("$.fechaFinal").isEqualTo("2027-02-28T00:00:00Z");
        respuesta.extractingPath("$.bloqueado").isEqualTo(1);
        respuesta.extractingPath("$.ordenContableBienesUso").isEqualTo(9);
        var capturado = org.mockito.ArgumentCaptor.forClass(Ejercicio.class);
        verify(update).updateEjercicio(eq(15), capturado.capture());
        assertThat(capturado.getValue()).isEqualTo(ejercicio());
    }

    @Test
    void postSinCamposLlamaAlAltaYDevuelve200() {
        when(create.createEjercicio(any())).thenReturn(ejercicio());
        mvc.post().uri("/ejercicio/").contentType(MediaType.APPLICATION_JSON).content("{}")
                .assertThat().hasStatus(HttpStatus.OK).bodyJson().extractingPath("$.ejercicioId").isEqualTo(15);
    }

    @Test
    void rechazosDeReglaTienen409ConCodigoYJson() {
        when(create.createEjercicio(any())).thenThrow(new EjercicioConflictoException(Codigo.SALTO, "Salto"));
        doThrow(new EjercicioConflictoException(Codigo.EN_USO, "En uso")).when(delete).deleteEjercicio(any());
        when(update.updateEjercicio(any(), any())).thenThrow(new EjercicioConflictoException(Codigo.CAMPO_INMUTABLE, "Inmutable"));
        var alta = mvc.post().uri("/ejercicio/").contentType(MediaType.APPLICATION_JSON).content("{\"ejercicioId\":17}")
                .assertThat().hasStatus(HttpStatus.CONFLICT).bodyJson();
        alta.extractingPath("$.status").isEqualTo(409);
        alta.extractingPath("$.codigo").isEqualTo("SALTO");
        alta.extractingPath("$.message").isEqualTo("Salto");
        mvc.delete().uri("/ejercicio/16").assertThat().hasStatus(HttpStatus.CONFLICT)
                .bodyJson().extractingPath("$.codigo").isEqualTo("EN_USO");
        mvc.put().uri("/ejercicio/15").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"x\"}")
                .assertThat().hasStatus(HttpStatus.CONFLICT).bodyJson().extractingPath("$.codigo").isEqualTo("CAMPO_INMUTABLE");
    }

    @Test
    void unFalloDeBloqueoDeLaBaseEs409ConCodigoConcurrencia() {
        when(create.createEjercicio(any())).thenThrow(new org.springframework.dao.CannotAcquireLockException("timeout"));
        var respuesta = mvc.post().uri("/ejercicio/").contentType(MediaType.APPLICATION_JSON).content("{}")
                .assertThat().hasStatus(HttpStatus.CONFLICT).bodyJson();
        respuesta.extractingPath("$.codigo").isEqualTo("CONCURRENCIA");
        respuesta.extractingPath("$.status").isEqualTo(409);
    }

    @Test
    void inexistenteEs404ConCuerpo() {
        when(update.updateEjercicio(any(), any())).thenThrow(new EjercicioNoEncontradoException(99));
        doThrow(new EjercicioNoEncontradoException(99)).when(delete).deleteEjercicio(99);
        var put = mvc.put().uri("/ejercicio/99").contentType(MediaType.APPLICATION_JSON).content("{\"bloqueado\":1}")
                .assertThat().hasStatus(HttpStatus.NOT_FOUND).bodyJson();
        put.extractingPath("$.status").isEqualTo(404);
        put.extractingPath("$.message").isEqualTo("Ejercicio 99 no encontrado");
        mvc.delete().uri("/ejercicio/99").assertThat().hasStatus(HttpStatus.NOT_FOUND).bodyJson()
                .extractingPath("$.codigo").isEqualTo("NO_ENCONTRADO");
    }

    @Test
    void solicitudMalformadaOInvalidaEs400ConCuerpo() {
        when(update.updateEjercicio(any(), any())).thenThrow(new EjercicioSolicitudInvalidaException("bloqueado debe ser 0 o 1"));
        mvc.put().uri("/ejercicio/15").contentType(MediaType.APPLICATION_JSON).content("{\"bloqueado\":2}")
                .assertThat().hasStatus(HttpStatus.BAD_REQUEST).bodyJson().extractingPath("$.codigo").isEqualTo("SOLICITUD_INVALIDA");
        var malformado = mvc.post().uri("/ejercicio/").contentType(MediaType.APPLICATION_JSON).content("{no es json")
                .assertThat().hasStatus(HttpStatus.BAD_REQUEST).bodyJson();
        malformado.extractingPath("$.status").isEqualTo(400);
        malformado.extractingPath("$.codigo").isEqualTo("SOLICITUD_INVALIDA");
    }

    @Test
    void bajaExitosaDevuelve204Vacio() {
        mvc.delete().uri("/ejercicio/16").assertThat().hasStatus(HttpStatus.NO_CONTENT).body().isEmpty();
        verify(delete).deleteEjercicio(16);
    }
}
