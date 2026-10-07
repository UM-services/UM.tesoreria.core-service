package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.application.service.ArticuloService;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.web.mapper.ArticuloDtoMapper;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@WebMvcTest(ArticuloController.class)
@Import(ArticuloDtoMapper.class)
class ArticuloControllerTest {

    private static final String GASTO = """
            {"articuloId": 10, "nombre": "Gasto", "tipo": "gasto", "directo": 0, "habilitado": 1}""";

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private ArticuloService service;

    @MockitoBean
    private CuentaService cuentaService;

    @Test
    void alta_201() {
        when(service.createArticulo(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(post("/articulo/", GASTO)).hasStatus(201).bodyJson().extractingPath("$.articuloId").isEqualTo(10);
    }

    @Test
    void alta_campoInvalido_400ConCodigoYCampo() {
        when(service.createArticulo(any())).thenThrow(new ArticuloValidationException("tipo", "tipo debe ser 'bien' o 'gasto'."));

        var respuesta = post("/articulo/", GASTO);

        assertThat(respuesta).hasStatus(400).hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("CAMPO_INVALIDO");
        assertThat(respuesta).bodyJson().extractingPath("$.campo").isEqualTo("tipo");
        assertThat(respuesta).bodyJson().extractingPath("$.detail").isEqualTo("tipo debe ser 'bien' o 'gasto'.");
    }

    @Test
    void alta_idDuplicado_409ConComoSeguir() {
        when(service.createArticulo(any())).thenThrow(ArticuloConflictException.idDuplicado(10L));

        var respuesta = post("/articulo/", GASTO);

        assertThat(respuesta).hasStatus(409);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("ARTICULO_ID_DUPLICADO");
        assertThat(respuesta).bodyJson().extractingPath("$.campo").isEqualTo("articuloId");
        assertThat(respuesta).bodyJson().extractingPath("$.detail").asString().contains("GET /articulo/10");
    }

    @Test
    void edicion_conflictoGenerico_409Conflicto() {
        when(service.updateArticulo(eq(16L), any())).thenThrow(ArticuloConflictException.conflicto(16L));

        var respuesta = mockMvc.put().uri("/articulo/16").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"x\"}").exchange();

        assertThat(respuesta).hasStatus(409).hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("CONFLICTO");
        assertThat(respuesta).bodyJson().extractingPath("$.detail").asString().contains("Releé").doesNotContain("Duplicate");
    }

    @Test
    void edicion_bloqueada_409ConflictoQuePideReintentar() {
        when(service.updateArticulo(eq(17L), any())).thenThrow(ArticuloConflictException.bloqueado(17L, false));

        var respuesta = mockMvc.put().uri("/articulo/17").contentType(MediaType.APPLICATION_JSON).content("{}").exchange();

        assertThat(respuesta).hasStatus(409);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("CONFLICTO");
        assertThat(respuesta).bodyJson().extractingPath("$.detail").asString().contains("tiene tomado el artículo 17");
    }

    @Test
    void contenidoNoJson_415ConElMismoContrato() {
        var respuesta = mockMvc.post().uri("/articulo/").contentType(MediaType.TEXT_PLAIN).content("hola").exchange();

        assertThat(respuesta).hasStatus(415).hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("TIPO_DE_CONTENIDO_NO_SOPORTADO");
        assertThat(respuesta).bodyJson().extractingPath("$.detail").isEqualTo("El cuerpo tiene que enviarse como application/json.");
        assertThat(respuesta).headers().containsHeader("Accept");
        verifyNoInteractions(service);
    }

    @Test
    void cuerpoMalFormado_400CuerpoInvalido() {
        var respuesta = post("/articulo/", "{\"articuloId\": ");

        assertThat(respuesta).hasStatus(400);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("CUERPO_INVALIDO");
        verifyNoInteractions(service);
    }

    @Test
    void byteDesbordado_400CuerpoInvalidoConCampo() {
        var respuesta = post("/articulo/", "{\"articuloId\": 10, \"tipo\": \"gasto\", \"habilitado\": 300}");

        assertThat(respuesta).hasStatus(400);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("CUERPO_INVALIDO");
        assertThat(respuesta).bodyJson().extractingPath("$.campo").isEqualTo("habilitado");
        verifyNoInteractions(service);
    }

    @Test
    void edicion_200() {
        when(service.updateArticulo(eq(10L), any())).thenReturn(Articulo.builder().articuloId(10L).habilitado((byte) 0).build());

        var respuesta = mockMvc.put().uri("/articulo/10").contentType(MediaType.APPLICATION_JSON).content("{\"habilitado\": 0}").exchange();

        assertThat(respuesta).hasStatusOk().bodyJson().extractingPath("$.habilitado").isEqualTo(0);
    }

    @Test
    void edicion_inexistente_404ConCuerpo() {
        when(service.updateArticulo(eq(11L), any())).thenThrow(new ArticuloException(11L));

        var respuesta = mockMvc.put().uri("/articulo/11").contentType(MediaType.APPLICATION_JSON).content("{}").exchange();

        assertThat(respuesta).hasStatus(404);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("ARTICULO_NO_ENCONTRADO");
    }

    @Test
    void lectura_inexistente_404ConCuerpo() {
        when(service.getArticuloById(12L)).thenReturn(Optional.empty());

        var respuesta = mockMvc.get().uri("/articulo/12").exchange();

        assertThat(respuesta).hasStatus(404);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("ARTICULO_NO_ENCONTRADO");
    }

    @Test
    void baja_204() {
        assertThat(mockMvc.delete().uri("/articulo/13").exchange()).hasStatus(204);
        verify(service).deleteArticulo(13L);
    }

    @Test
    void baja_inexistente_404ConCuerpo() {
        doThrow(new ArticuloException(14L)).when(service).deleteArticulo(14L);

        var respuesta = mockMvc.delete().uri("/articulo/14").exchange();

        assertThat(respuesta).hasStatus(404);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("ARTICULO_NO_ENCONTRADO");
    }

    @Test
    void baja_referenciada_409ConReferencias() {
        doThrow(ArticuloConflictException.referenciado(15L, List.of(new ReferenciaArticulo("entrega_detalle", null))))
                .when(service).deleteArticulo(15L);

        var respuesta = mockMvc.delete().uri("/articulo/15").exchange();

        assertThat(respuesta).hasStatus(409);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("ARTICULO_REFERENCIADO");
        assertThat(respuesta).bodyJson().extractingPath("$.referencias[0].tabla").isEqualTo("entrega_detalle");
        assertThat(respuesta).bodyJson().extractingPath("$.detail").asString().contains("habilitado = 0").doesNotContain("FOREIGN KEY");
    }

    @Test
    void idNoNumerico_400ParametroInvalido() {
        var respuesta = mockMvc.delete().uri("/articulo/abc").exchange();

        assertThat(respuesta).hasStatus(400);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("PARAMETRO_INVALIDO");
        assertThat(respuesta).bodyJson().extractingPath("$.campo").isEqualTo("id");
    }

    @Test
    void errorNoMapeado_500SinSqlNiRestriccion() {
        when(service.createArticulo(any())).thenThrow(new DataIntegrityViolationException(
                "could not execute statement [Data too long for column 'Art_Nombre'] [insert into articulos ...]"));

        var respuesta = post("/articulo/", GASTO);

        assertThat(respuesta).hasStatus(500);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("ERROR_INTERNO");
        assertThat(respuesta).bodyText().doesNotContain("insert").doesNotContain("Art_Nombre");
    }

    @Test
    void errorDeSpringConEstado5xx_detalleGenericoSinElMotivoInterno() {
        when(service.createArticulo(any())).thenThrow(new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE, "pool agotado: select * from articulos"));

        var respuesta = post("/articulo/", GASTO);

        assertThat(respuesta).hasStatus(503);
        assertThat(respuesta).bodyJson().extractingPath("$.codigo").isEqualTo("ERROR_INTERNO");
        assertThat(respuesta).bodyText().doesNotContain("select").doesNotContain("pool");
    }

    @Test
    void metodoNoSoportado_loResuelveSpring() {
        assertThat(mockMvc.patch().uri("/articulo/1").exchange()).hasStatus(405);
    }

    private org.springframework.test.web.servlet.assertj.MvcTestResult post(String uri, String json) {
        return mockMvc.post().uri(uri).contentType(MediaType.APPLICATION_JSON).content(json).exchange();
    }
}
