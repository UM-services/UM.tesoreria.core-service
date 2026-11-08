package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.exception.ImportacionPagoException;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.service.ImportacionPagoService;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.CanalImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.PreviewImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoEliminacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoReacreditacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.SolicitudImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.EliminarImportacionUseCase;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.FindNoRegistradosUseCase;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.ImportarArchivoUseCase;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.ListarImportacionesUseCase;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.PreviewImportacionUseCase;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.mapper.ImportacionPagoDtoMapper;
import um.tesoreria.core.service.facade.PayPerTicFileService;
import um.tesoreria.core.util.transfer.FileInfo;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@WebMvcTest(ImportacionPagoController.class)
@Import(ImportacionPagoDtoMapper.class)
class ImportacionPagoControllerTest {

    private static final String BASE = "/api/tesoreria/core/cobranza/importacion";

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private PreviewImportacionUseCase previewImportacionUseCase;
    @MockitoBean
    private ImportarArchivoUseCase importarArchivoUseCase;
    @MockitoBean
    private EliminarImportacionUseCase eliminarImportacionUseCase;
    @MockitoBean
    private FindNoRegistradosUseCase findNoRegistradosUseCase;
    @MockitoBean
    private ListarImportacionesUseCase listarImportacionesUseCase;
    @MockitoBean
    private ImportacionPagoService importacionPagoService;
    @MockitoBean
    private PayPerTicFileService payPerTicFileService;

    @Test
    void previewDeArchivoTomaElNombreDelMultipart() {
        when(previewImportacionUseCase.preview(any())).thenReturn(PreviewImportacion.builder()
                .filename("rapipago.txt")
                .canal(CanalImportacion.RAPIPAGO)
                .registros(1)
                .total(new BigDecimal("123.45"))
                .lineas(List.of())
                .build());

        var respuesta = mockMvc.perform(multipart(BASE + "/rapipago/preview")
                .file(archivo("rapipago.txt", "contenido")));

        assertThat(respuesta).hasStatusOk();
        assertThat(respuesta).bodyJson().extractingPath("$.canal").isEqualTo("RAPIPAGO");
        ArgumentCaptor<SolicitudImportacion> solicitud = ArgumentCaptor.forClass(SolicitudImportacion.class);
        verify(previewImportacionUseCase).preview(solicitud.capture());
        assertThat(solicitud.getValue().canal()).isEqualTo(CanalImportacion.RAPIPAGO);
        assertThat(solicitud.getValue().filename()).isEqualTo("rapipago.txt");
        assertThat(solicitud.getValue().contenido()).isNotEmpty();
    }

    @Test
    void previewDePayPerTicAceptaPartesDeTextoYNormalizaElPeriodo() {
        when(previewImportacionUseCase.preview(any())).thenReturn(PreviewImportacion.builder()
                .filename("rendicion.xlsx")
                .canal(CanalImportacion.PAY_PER_TIC)
                .registros(0)
                .total(BigDecimal.ZERO)
                .lineas(List.of())
                .build());

        var respuesta = mockMvc.perform(multipart(BASE + "/pay-per-tic/preview")
                .part(texto("filename", "rendicion.xlsx"))
                .part(texto("desde", "2026-03-01"))
                .part(texto("hasta", "2026-03-31")));

        assertThat(respuesta).hasStatusOk();
        ArgumentCaptor<SolicitudImportacion> solicitud = ArgumentCaptor.forClass(SolicitudImportacion.class);
        verify(previewImportacionUseCase).preview(solicitud.capture());
        assertThat(solicitud.getValue().contenido()).isNull();
        assertThat(solicitud.getValue().filename()).isEqualTo("rendicion.xlsx");
        assertThat(solicitud.getValue().desde()).isEqualTo(OffsetDateTime.parse("2026-03-01T00:00:00Z"));
        assertThat(solicitud.getValue().hasta().toLocalDate()).isEqualTo(java.time.LocalDate.parse("2026-03-31"));
        assertThat(solicitud.getValue().hasta().getHour()).isEqualTo(23);
    }

    @Test
    void importarLeeLasLineasAceptadasYElFlagDeDuplicados() {
        when(importarArchivoUseCase.importar(any())).thenReturn(ResultadoImportacion.builder()
                .filename("rapipago.txt")
                .canal(CanalImportacion.RAPIPAGO)
                .total(new BigDecimal("123.45"))
                .build());

        var respuesta = mockMvc.perform(multipart(BASE + "/rapipago")
                .file(archivo("rapipago.txt", "contenido"))
                .part(texto("lineasAceptadas", "2,3"))
                .part(texto("permitirDuplicados", "true")));

        assertThat(respuesta).hasStatusOk();
        ArgumentCaptor<SolicitudImportacion> solicitud = ArgumentCaptor.forClass(SolicitudImportacion.class);
        verify(importarArchivoUseCase).importar(solicitud.capture());
        assertThat(solicitud.getValue().lineasAceptadas()).containsExactly(2, 3);
        assertThat(solicitud.getValue().permitirDuplicados()).isTrue();
    }

    @Test
    void reacreditarRecibeElArchivoYElIdDeLaImportacion() {
        when(importacionPagoService.reacreditar(eq(17), any())).thenReturn(
                new ResultadoReacreditacion("rapipago.txt", CanalImportacion.RAPIPAGO, 1, 1, 0, 0, List.of()));

        var respuesta = mockMvc.perform(multipart(BASE + "/rapipago/17/reacreditar")
                .file(archivo("rapipago.txt", "contenido")));

        assertThat(respuesta).hasStatusOk();
        assertThat(respuesta).bodyJson().extractingPath("$.actualizados").isEqualTo(1);
        ArgumentCaptor<SolicitudImportacion> solicitud = ArgumentCaptor.forClass(SolicitudImportacion.class);
        verify(importacionPagoService).reacreditar(eq(17), solicitud.capture());
        assertThat(solicitud.getValue().filename()).isEqualTo("rapipago.txt");
        assertThat(solicitud.getValue().canal()).isEqualTo(CanalImportacion.RAPIPAGO);
    }

    @Test
    void buscarAceptaElCodigoDelCanalConGuionBajo() {
        when(listarImportacionesUseCase.listar(CanalImportacion.PAY_PER_TIC)).thenReturn(List.of());

        var respuesta = mockMvc.post().uri(BASE + "/buscar")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{\"canal\":\"PAY_PER_TIC\"}")
                .exchange();

        assertThat(respuesta).hasStatusOk();
        verify(listarImportacionesUseCase).listar(CanalImportacion.PAY_PER_TIC);
    }

    @Test
    void rechazosDevuelveLosDebitosRechazadosDeLaImportacion() {
        when(importacionPagoService.rechazos(17)).thenReturn(List.of(
                new um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.Rechazo(
                        55L, 6, 2, 123L, 1, 1, 1, new BigDecimal("123.45"),
                        OffsetDateTime.parse("2026-03-15T00:00:00Z"), "TARJETA VENCIDA", 10L)));

        var respuesta = mockMvc.get().uri(BASE + "/17/rechazos").exchange();

        assertThat(respuesta).hasStatusOk();
        assertThat(respuesta).bodyJson().extractingPath("$[0].debitoId").isEqualTo(55);
        assertThat(respuesta).bodyJson().extractingPath("$[0].motivoRechazo").isEqualTo("TARJETA VENCIDA");
    }

    @Test
    void subirPayPerTicDelegaElXlsxComoBase64() {
        doNothing().when(payPerTicFileService).upload(any());

        var respuesta = mockMvc.perform(multipart(BASE + "/pay-per-tic/archivo")
                .file(archivo("rendicion.xlsx", "abc")));

        assertThat(respuesta).hasStatus(204);
        ArgumentCaptor<FileInfo> fileInfo = ArgumentCaptor.forClass(FileInfo.class);
        verify(payPerTicFileService).upload(fileInfo.capture());
        assertThat(fileInfo.getValue().getFilename()).isEqualTo("rendicion.xlsx");
        assertThat(new String(java.util.Base64.getDecoder().decode(fileInfo.getValue().getBase64()),
                StandardCharsets.UTF_8)).isEqualTo("abc");
    }

    @Test
    void eliminarDevuelveElResultadoDeLaImportacion() {
        when(eliminarImportacionUseCase.eliminar(17)).thenReturn(ResultadoEliminacion.builder()
                .filename("pmc.txt")
                .pagosEliminados(3)
                .pagosReemplazoEliminados(0)
                .archivosBancoEliminados(2)
                .cuotasRecalculadas(2)
                .build());

        var respuesta = mockMvc.delete().uri(BASE + "/17").exchange();

        assertThat(respuesta).hasStatusOk();
        assertThat(respuesta).bodyJson().extractingPath("$.pagosEliminados").isEqualTo(3);
        assertThat(respuesta).bodyJson().extractingPath("$.filename").isEqualTo("pmc.txt");
        verify(eliminarImportacionUseCase).eliminar(17);
    }

    @Test
    void eliminarUnaImportacionInexistenteDevuelve404() {
        when(eliminarImportacionUseCase.eliminar(99))
                .thenThrow(new ImportacionPagoException("No existe la importación 99"));

        var respuesta = mockMvc.delete().uri(BASE + "/99").exchange();

        assertThat(respuesta).hasStatus(404);
    }

    private MockMultipartFile archivo(String nombre, String contenido) {
        return new MockMultipartFile("file", nombre, "text/plain", contenido.getBytes(StandardCharsets.UTF_8));
    }

    private MockPart texto(String nombre, String contenido) {
        return new MockPart(nombre, contenido.getBytes(StandardCharsets.UTF_8));
    }

}
