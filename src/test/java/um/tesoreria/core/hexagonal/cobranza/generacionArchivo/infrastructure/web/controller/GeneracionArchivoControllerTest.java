package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CanalGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.PreviewGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoOperacionDebito;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.AnularEnvioDebitoUseCase;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.GenerarArchivoUseCase;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.PreviewGeneracionUseCase;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.ReprogramarVencimientoDebitoUseCase;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.mapper.GeneracionArchivoDtoMapper;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(GeneracionArchivoController.class)
@Import(GeneracionArchivoDtoMapper.class)
class GeneracionArchivoControllerTest {

    private static final String BASE = "/api/tesoreria/core/cobranza/generacion";

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private PreviewGeneracionUseCase previewGeneracionUseCase;
    @MockitoBean
    private GenerarArchivoUseCase generarArchivoUseCase;
    @MockitoBean
    private AnularEnvioDebitoUseCase anularEnvioDebitoUseCase;
    @MockitoBean
    private ReprogramarVencimientoDebitoUseCase reprogramarVencimientoDebitoUseCase;

    @Test
    void canalesDevuelveLosSeisCanales() {
        var respuesta = mockMvc.get().uri(BASE + "/canal").exchange();

        assertThat(respuesta).hasStatusOk();
        assertThat(respuesta).bodyJson().extractingPath("$.length()").isEqualTo(6);
    }

    @Test
    void previewConvierteLasFechasDelBody() {
        when(previewGeneracionUseCase.preview(any())).thenReturn(new PreviewGeneracion(
                CanalGeneracion.PMC, 1, new BigDecimal("123.45"), 0, List.of(), List.of()));

        var respuesta = mockMvc.post().uri(BASE + "/pmc/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"desde\":\"2026-03-01\",\"hasta\":\"2026-03-31\"}")
                .exchange();

        assertThat(respuesta).hasStatusOk();
        assertThat(respuesta).bodyJson().extractingPath("$.registros").isEqualTo(1);
        assertThat(respuesta).bodyJson().extractingPath("$.canal").isEqualTo("PMC");
    }

    @Test
    void generarDevuelveElArchivoConSuNombreYContentType() throws Exception {
        byte[] contenido = "abc".getBytes(StandardCharsets.UTF_8);
        when(generarArchivoUseCase.generar(any())).thenReturn(new ResultadoGeneracion(
                "PMC_20260315.txt", "text/plain", contenido, 1, new BigDecimal("123.45")));

        var respuesta = mockMvc.post().uri(BASE + "/pmc")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"desde\":\"2026-03-01\",\"hasta\":\"2026-03-31\"}")
                .exchange();

        assertThat(respuesta).hasStatusOk();
        assertThat(respuesta.getResponse().getHeader("Content-Disposition"))
                .contains("PMC_20260315.txt");
        assertThat(respuesta.getResponse().getContentType()).contains("text/plain");
        assertThat(respuesta.getResponse().getContentAsString()).isEqualTo("abc");
    }

    @Test
    void anulacionDeDebitoDirectoDevuelveElConteo() {
        when(anularEnvioDebitoUseCase.anular(any()))
                .thenReturn(new ResultadoOperacionDebito("ANULACION", 1, 0));

        var respuesta = mockMvc.post().uri(BASE + "/debito-directo/anulacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fechaEnvio\":\"2026-03-15\"}")
                .exchange();

        assertThat(respuesta).hasStatusOk();
        assertThat(respuesta).bodyJson().extractingPath("$.registros").isEqualTo(1);
        verify(anularEnvioDebitoUseCase).anular(any());
    }

    @Test
    void reprogramacionDevuelveElConteo() {
        when(reprogramarVencimientoDebitoUseCase.reprogramar(any()))
                .thenReturn(new ResultadoOperacionDebito("REPROGRAMACION", 2, 1));

        var respuesta = mockMvc.post().uri(BASE + "/debito-directo/reprogramacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"desde\":\"2026-03-01\",\"hasta\":\"2026-03-31\","
                        + "\"fechaVencimiento\":\"2026-04-01\"}")
                .exchange();

        assertThat(respuesta).hasStatusOk();
        assertThat(respuesta).bodyJson().extractingPath("$.registros").isEqualTo(2);
        assertThat(respuesta).bodyJson().extractingPath("$.omitidos").isEqualTo(1);
    }

}
